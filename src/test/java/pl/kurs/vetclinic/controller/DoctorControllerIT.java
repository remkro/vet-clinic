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
import pl.kurs.vetclinic.command.AddDoctorCommand;
import pl.kurs.vetclinic.model.dto.DoctorExtendedDto;
import pl.kurs.vetclinic.repository.DoctorRepository;
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
class DoctorControllerIT {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @AfterEach
    void cleanUp() {
        doctorRepository.deleteAll();
    }

    @Test
    void shouldAddDoctor_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Stefan"))
                .andExpect(jsonPath("$.lastName").value("Dudek"))
                .andExpect(jsonPath("$.medType").value("chirurg"))
                .andExpect(jsonPath("$.petType").value("kot"))
                .andExpect(jsonPath("$.hourlyRate").value(50))
                .andExpect(jsonPath("$.nip").value("1212571104"))
                .andExpect(jsonPath("$.currentlyEmployed").value(true));
    }

    @Test
    void shouldGetSingleDoctor_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        DoctorExtendedDto addedDoctor = mapper.readValue(result.getResponse().getContentAsString(), DoctorExtendedDto.class);
        Long id = addedDoctor.getId();

        postman.perform(MockMvcRequestBuilders.get("/doctor/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Stefan"))
                .andExpect(jsonPath("$.lastName").value("Dudek"))
                .andExpect(jsonPath("$.medType").value("chirurg"))
                .andExpect(jsonPath("$.petType").value("kot"))
                .andExpect(jsonPath("$.hourlyRate").value(50))
                .andExpect(jsonPath("$.nip").value("1212571104"))
                .andExpect(jsonPath("$.currentlyEmployed").value(true));
    }

    @Test
    void shouldGetAllDoctorsDefault_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand1 = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson1 = mapper.writeValueAsString(addDoctorCommand1);
        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson1));

        AddDoctorCommand addDoctorCommand2 = new AddDoctorCommand("Zenon", "Sobczak",
                "urolog", "komar", 5, "3561623987", true);
        String addDoctorCommandJson2 = mapper.writeValueAsString(addDoctorCommand2);
        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson2));

        AddDoctorCommand addDoctorCommand3 = new AddDoctorCommand("Grzegorz", "Waligrucha",
                "neurolog", "chomik", 5, "9717886966", true);
        String addDoctorCommandJson3 = mapper.writeValueAsString(addDoctorCommand3);
        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson3));

        MvcResult result = postman.perform(MockMvcRequestBuilders.get("/doctor/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<DoctorExtendedDto> doctors = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<DoctorExtendedDto>>() {
                });
        assertEquals(3, doctors.size());
        assertEquals("Stefan", doctors.get(0).getFirstName());
        assertEquals("Dudek", doctors.get(0).getLastName());
        assertEquals("Zenon", doctors.get(1).getFirstName());
        assertEquals("Sobczak", doctors.get(1).getLastName());
        assertEquals("Grzegorz", doctors.get(2).getFirstName());
        assertEquals("Waligrucha", doctors.get(2).getLastName());
    }

    @Test
    void shouldGetAllDoctorsWithPaginationParameters_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand1 = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson1 = mapper.writeValueAsString(addDoctorCommand1);
        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson1));

        AddDoctorCommand addDoctorCommand2 = new AddDoctorCommand("Zenon", "Sobczak",
                "urolog", "komar", 5, "3561623987", true);
        String addDoctorCommandJson2 = mapper.writeValueAsString(addDoctorCommand2);
        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson2));

        AddDoctorCommand addDoctorCommand3 = new AddDoctorCommand("Grzegorz", "Waligrucha",
                "neurolog", "chomik", 5, "9717886966", true);
        String addDoctorCommandJson3 = mapper.writeValueAsString(addDoctorCommand3);
        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson3));

        MvcResult result = postman.perform(MockMvcRequestBuilders.get("/doctor/?page=0&size=2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<DoctorExtendedDto> doctors = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<DoctorExtendedDto>>() {
                });
        assertEquals(2, doctors.size());
        assertEquals("Stefan", doctors.get(0).getFirstName());
        assertEquals("Dudek", doctors.get(0).getLastName());
        assertEquals("Zenon", doctors.get(1).getFirstName());
        assertEquals("Sobczak", doctors.get(1).getLastName());
    }

    @Test
    void shouldFireSingleDoctor_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);
        MvcResult addDoctorResult = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andReturn();
        String postResponseAsString = addDoctorResult.getResponse().getContentAsString();
        DoctorExtendedDto addedDoctor = mapper.readValue(postResponseAsString, DoctorExtendedDto.class);
        Long id = addedDoctor.getId();

        postman.perform(MockMvcRequestBuilders.put("/doctor/{id}/fire", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Stefan"))
                .andExpect(jsonPath("$.lastName").value("Dudek"))
                .andExpect(jsonPath("$.currentlyEmployed").value(false));
    }

    @Test
    void shouldFailAddingDoctorWhenFirstNameIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand(null, "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingDoctorWhenLastNameIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingDoctorWhenMedicalSpecializationIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingDoctorWhenMedicalSpecializationIsUnsupported_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "anastezjolog", "kot", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("MEDICAL_SPECIALIZATION_NOT_SUPPORTED"));
    }

    @Test
    void shouldFailAddingDoctorWhenPetTypeIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingDoctorWhenPetTypeIsUnsupported_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kuna", 50, "1212571104", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("ANIMAL_NOT_SUPPORTED"));
    }

    @Test
    void shouldFailAddingDoctorWhenNipIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("NIP_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailAddingDoctorWhenNipIsDuplicated_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand1 = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "1212571104", true);
        String addDoctorCommandJson1 = mapper.writeValueAsString(addDoctorCommand1);

        postman.perform(MockMvcRequestBuilders.post("/doctor")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDoctorCommandJson1));

        AddDoctorCommand addDoctorCommand2 = new AddDoctorCommand("Henryk", "Grzebielucha",
                "urolog", "kot", 45, "1212571104", true);
        String addDoctorCommandJson2 = mapper.writeValueAsString(addDoctorCommand2);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("NIP_CANNOT_BE_DUPLICATED"));
    }

    @Test
    void shouldFailAddingDoctorWhenNipIsIncorrect_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "123", true);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("INVALID_NIP"));
    }

    @Test
    void shouldFailAddingDoctorWhenEmployedStatusIsEmpty_JwtTokenAuthenticated() throws Exception {
        String token = getToken();

        AddDoctorCommand addDoctorCommand = new AddDoctorCommand("Stefan", "Dudek",
                "chirurg", "kot", 50, "", null);
        String addDoctorCommandJson = mapper.writeValueAsString(addDoctorCommand);

        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/doctor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addDoctorCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        assertTrue(responseAsString.contains("FIELD_CANNOT_BE_EMPTY"));
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