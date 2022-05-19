package pl.kurs.vetclinic.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.vetclinic.VetClinicApplication;
import pl.kurs.vetclinic.command.AddPatientCommand;
import pl.kurs.vetclinic.model.dto.DoctorExtendedDto;
import pl.kurs.vetclinic.model.dto.PatientDto;
import pl.kurs.vetclinic.repository.PatientRepository;
import pl.kurs.vetclinic.security.jwt.JwtRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VetClinicApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class PatientControllerIT {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PatientRepository patientRepository;

    @AfterEach
    void cleanUp() {
        patientRepository.deleteAll();
    }

    @Test
    void shouldAddPatient_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Krzysztof"))
                .andExpect(jsonPath("$.lastName").value("Kononowicz"))
                .andExpect(jsonPath("$.email").value("krzysio@misio.pl"))
                .andExpect(jsonPath("$.petName").value("azorek"))
                .andExpect(jsonPath("$.petBreed").value("pies"));
    }

    @Test
    void shouldGetSinglePatient_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andReturn();
        PatientDto addedPatient = mapper.readValue(result.getResponse().getContentAsString(), PatientDto.class);
        Long id = addedPatient.getId();

        postman.perform(MockMvcRequestBuilders.get("/patient/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value("Krzysztof"))
                .andExpect(jsonPath("$.lastName").value("Kononowicz"))
                .andExpect(jsonPath("$.email").value("krzysio@misio.pl"))
                .andExpect(jsonPath("$.petName").value("azorek"))
                .andExpect(jsonPath("$.petBreed").value("pies"));
    }

    @Test
    void shouldGetAllPatientsDefault_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand1 = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson1 = mapper.writeValueAsString(addPatientCommand1);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson1));

        AddPatientCommand addPatientCommand2 = new AddPatientCommand("Wojciech", "Suchodolski",
                "wojciech@zbombasu.pl", "kicia", "kot");
        String addPatientCommandJson2 = mapper.writeValueAsString(addPatientCommand2);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson2));

        AddPatientCommand addPatientCommand3 = new AddPatientCommand("Marcin", "Madrzak",
                "marcinmadrzak@gmail.com", "zdziuchu", "komar");
        String addPatientCommandJson3 = mapper.writeValueAsString(addPatientCommand3);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson3));

        MvcResult result = postman.perform(MockMvcRequestBuilders.get("/patient/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<PatientDto> patients = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<PatientDto>>() {
                });
        assertEquals(3, patients.size());
        assertEquals("Krzysztof", patients.get(0).getFirstName());
        assertEquals("Kononowicz", patients.get(0).getLastName());
        assertEquals("Wojciech", patients.get(1).getFirstName());
        assertEquals("Suchodolski", patients.get(1).getLastName());
        assertEquals("Marcin", patients.get(2).getFirstName());
        assertEquals("Madrzak", patients.get(2).getLastName());
    }

    @Test
    void shouldGetAllPatientsWithPaginationParameters_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand1 = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson1 = mapper.writeValueAsString(addPatientCommand1);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson1));

        AddPatientCommand addPatientCommand2 = new AddPatientCommand("Wojciech", "Suchodolski",
                "wojciech@zbombasu.pl", "kicia", "kot");
        String addPatientCommandJson2 = mapper.writeValueAsString(addPatientCommand2);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson2));

        AddPatientCommand addPatientCommand3 = new AddPatientCommand("Marcin", "Madrzak",
                "marcinmadrzak@gmail.com", "zdziuchu", "komar");
        String addPatientCommandJson3 = mapper.writeValueAsString(addPatientCommand3);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson3));

        AddPatientCommand addPatientCommand4 = new AddPatientCommand("Tomasz", "Meloch",
                "tomaszmeloch@gmail.com", "agnieszka", "mysz");
        String addPatientCommandJson4 = mapper.writeValueAsString(addPatientCommand4);
        postman.perform(MockMvcRequestBuilders.post("/patient")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPatientCommandJson4));

        MvcResult result = postman.perform(MockMvcRequestBuilders.get("/patient/?page=1&size=2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<DoctorExtendedDto> doctors = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<DoctorExtendedDto>>() {
                });
        assertEquals(2, doctors.size());
        assertEquals("Marcin", doctors.get(0).getFirstName());
        assertEquals("Madrzak", doctors.get(0).getLastName());
        assertEquals("Tomasz", doctors.get(1).getFirstName());
        assertEquals("Meloch", doctors.get(1).getLastName());
    }

    @Test
    void shouldFailAddingPatientWhenFirstNameIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("", "Kononowicz",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingPatientWhenLastNameIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "",
                "krzysio@misio.pl", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingPatientWhenEmailIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingPatientWhenEmailIsIncorrect_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "wrongmail", "azorek", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("INVALID_EMAIL"));
    }

    @Test
    void shouldFailAddingPatientWhenPetNameIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "", "pies");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingPatientWhenPetTypeIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingPatientWhenPetTypeIsUnsupported_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddPatientCommand addPatientCommand = new AddPatientCommand("Krzysztof", "Kononowicz",
                "krzysio@misio.pl", "azorek", "jaszczomb");
        String addPatientCommandJson = mapper.writeValueAsString(addPatientCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/patient")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addPatientCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("ANIMAL_NOT_SUPPORTED"));
    }

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

}