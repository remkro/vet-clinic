package pl.kurs.vetclinic.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import pl.kurs.vetclinic.VetClinicApplication;
import pl.kurs.vetclinic.command.AddDoctorCommand;
import pl.kurs.vetclinic.command.AddPatientCommand;
import pl.kurs.vetclinic.command.AddVisitCommand;
import pl.kurs.vetclinic.command.CheckVisitCommand;
import pl.kurs.vetclinic.model.dto.DoctorExtendedDto;
import pl.kurs.vetclinic.model.dto.PatientDto;
import pl.kurs.vetclinic.model.dto.VisitExtendedDto;
import pl.kurs.vetclinic.model.dto.VisitSimpleDto;
import pl.kurs.vetclinic.repository.DoctorRepository;
import pl.kurs.vetclinic.repository.PatientRepository;
import pl.kurs.vetclinic.repository.VisitRepository;
import pl.kurs.vetclinic.security.jwt.JwtRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@SpringBootTest(classes = VetClinicApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class VisitControllerIT {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @AfterEach
    void cleanUp() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("sender", "springboot"));

    @Test
    void shouldAddVisit_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldConfirmVisit_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult addVisitResult = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andReturn();
        VisitSimpleDto visit = mapper.readValue(addVisitResult.getResponse().getContentAsString(), VisitSimpleDto.class);

        postman.perform(MockMvcRequestBuilders.get("/visit/{id}/confirm", visit.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VISIT_CONFIRMED"));
    }

    @Test
    void shouldCancelVisit_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult addVisitResult = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andReturn();
        VisitSimpleDto visit = mapper.readValue(addVisitResult.getResponse().getContentAsString(), VisitSimpleDto.class);

        postman.perform(MockMvcRequestBuilders.get("/visit/{id}/cancel", visit.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VISIT_CANCELED"));
    }

    @Test
    void shouldCheckNearestVisits_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand1 = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson1 = mapper.writeValueAsString(addVisitCommand1);
        postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson1))
                .andReturn();

        AddVisitCommand addVisitCommand2 = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(14) + " " + "16:00:00");
        String addVisitCommandJson2 = mapper.writeValueAsString(addVisitCommand2);
        postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson2))
                .andReturn();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "pies",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isOk())
                .andReturn();
        List<VisitExtendedDto> visits = mapper.readValue(checkVisitsResult.getResponse().getContentAsString(),
                new TypeReference<List<VisitExtendedDto>>() {
                });
        assertEquals(2, visits.size());
        assertTrue(visits.get(0).getDate().isBefore(visits.get(1).getDate()));
    }

    @Test
    void shouldShouldFailAddingVisitWhenDoctorIdIsNull_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(null, patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_NULL"));
    }

    @Test
    void shouldShouldFailAddingVisitWhenDoctorNotExists_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(1000L, patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("DOCTOR_ID_NOT_FOUND"));
    }

    @Test
    void shouldShouldFailAddingVisitWhenDoctorIsUnemployed_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", false);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("DOCTOR_UNEMPLOYED"));
    }

    @Test
    void shouldShouldFailAddingVisitWhenPatientIdIsNull_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), null,
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_NULL"));
    }

    @Test
    void shouldShouldFailAddingVisitWhenPatientIdNotExists_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), 200L,
                getSoonestNonWeekendDateWithOffset(2) + " " + "14:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("PATIENT_ID_NOT_FOUND"));
    }

    @Test
    void shouldFailAddingVisitWhenDateIsIncorrect_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(), "15-17-345 25:56:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("INVALID_DATE"));
    }

    @Test
    void shouldFailAddingVisitWhenDateIsPast_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(), "12-12-2005 13:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("CANNOT_BOOK_VISIT_IN_PAST"));
    }

    @Test
    void shouldFailAddingVisitWhenDateIsTomorrow_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(), getTomorrowDate()
                + " " + "12:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("CANNOT_BOOK_VISIT_ONE_DAY_BEFORE_REQUESTED_DATE"));
    }

    @Test
    void shouldFailAddingVisitWhenDateOutsideWorkingHours_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "23:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("CANNOT_BOOK_VISIT_OUTSIDE_WORKING_HOURS"));
    }

    @Test
    void shouldFailAddingVisitWhenDateAlreadyBooked_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "pies", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);

        AddVisitCommand addVisitCommand = new AddVisitCommand(doctor.getId(), patient.getId(),
                getSoonestNonWeekendDateWithOffset(2) + " " + "15:00:00");
        String addVisitCommandJson = mapper.writeValueAsString(addVisitCommand);
        postman.perform(MockMvcRequestBuilders.post("/visit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addVisitCommandJson));
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/visit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addVisitCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("DATE_ALREADY_BOOKED"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenMedicalSpecializationIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("", "pies",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenMedicalSpecializationIsUnsupported_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("psychoterapeuta", "pies",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("MEDICAL_SPECIALIZATION_NOT_SUPPORTED"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenAnimalIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenAnimalIsUnsupported_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "jeżozwierz",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("ANIMAL_NOT_SUPPORTED"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenFromDateIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "jeżozwierz",
                null,
                LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenFromDateIsIncorrect_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "pies",
                "16-17-223", LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER));
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("INVALID_DATE"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenToDateIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "jeżozwierz",
                LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailCheckingNearestVisitsWhenToDateIsIncorrect_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        CheckVisitCommand checkVisitCommand = new CheckVisitCommand("chirurg", "jeżozwierz",
                LocalDateTime.now().format(DATE_TIME_FORMATTER), "20-30-777");
        String checkVisitCommandJson = mapper.writeValueAsString(checkVisitCommand);
        MvcResult checkVisitsResult = postman.perform(MockMvcRequestBuilders.
                        post("/visit/check?results={results}", 2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkVisitCommandJson))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = checkVisitsResult.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("INVALID_DATE"));
    }

//    @Test
//    void shouldAddOnlyOneVisitWhenTryingToAddMultipleVisitsAtTheSameTime_JwtTokenAuthenticated() throws Exception {
//        String token = getToken();
//
//        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
//                "chirurg", "pies", 50, "1212571104", true);
//        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
//        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(addDoctorCommandJson))
//                .andReturn();
//        DoctorExtendedDto doctor = mapper.readValue(addDoctorResult.getResponse().getContentAsString(), DoctorExtendedDto.class);
//
//        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
//                "krzysio@misio.pl", "azorek", "pies");
//        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);
//        MvcResult addPatientResult = postman.perform(MockMvcRequestBuilders.post("/patient")
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(addPatientCommandJson))
//                .andReturn();
//        PatientDto patient = mapper.readValue(addPatientResult.getResponse().getContentAsString(), PatientDto.class);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/attack", String.class);
//
//        TimeUnit.MINUTES.sleep(3);
//        assertEquals(100, visitRepository.findAll().size());
//    }

    private String getToken() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("remix", "dupa");
        String jwt = mapper.writeValueAsString(jwtRequest);
        MvcResult jwtTokenRequestResult = postman.perform(MockMvcRequestBuilders.post("/authenticate")
                        .with(httpBasic("remix", "dupa"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jwt))
                .andReturn();
        String jwtTokenResponseContent = jwtTokenRequestResult.getResponse().getContentAsString();
        jwtTokenResponseContent = jwtTokenResponseContent.replace("{\"token\":\"", "");
        return jwtTokenResponseContent.replace("\"}", "").trim();
    }

    private String getSoonestNonWeekendDateWithOffset(int offset) {
        LocalDate date = LocalDate.now().plusDays(offset);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String getTomorrowDate() {
        return LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

}