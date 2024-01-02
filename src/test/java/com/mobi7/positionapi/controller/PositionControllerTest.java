package com.mobi7.positionapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.service.PositionService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class PositionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private PositionService serviceMock;

    @Autowired
    private ObjectMapper jsonMapper;
    private static EasyRandom generator;


    @Value("classpath:model/position-v1_0_00.json")
    private Resource positionTemplate;


    @BeforeEach
    void before() {
        // Reset mocks
        reset(serviceMock);
    }

    @BeforeAll
    public static void beforAll() {
        // Random feature generator
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(2, 6);
        generator = new EasyRandom(parameters);
    }

    private PositionRequest randomPositionRequest() {
        return generator.nextObject(PositionRequest.class);
    }


    public Position modelPosition() throws JsonProcessingException, IOException {
        JsonNode template = jsonMapper.readTree(positionTemplate.getFile());
        return jsonMapper.treeToValue(template, Position.class);
    }


    private String asJson(Object obj) {
        try {
            return jsonMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("Should create position on H2 database with success")
    void createPositionTest() throws Exception {
        // Fixtures
        PositionRequest request = randomPositionRequest();
        ModelMapper modelMapper = new ModelMapper();
//        Position position = modelPosition();

        Position position = modelMapper.map(request, Position.class);
        position.buildId();

        // Mocks
        when(serviceMock.create(any(PositionRequest.class))).thenReturn(position);

        // Test
        mockMvc.perform(post("/position")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("positionId").exists())
                .andExpect(jsonPath("plate").value(position.getPlate()))
                .andExpect(jsonPath("datePosition").value(position.getDatePosition().toString()))
                .andExpect(jsonPath("speed").value(position.getSpeed()))
                .andExpect(jsonPath("longitude").value(position.getLongitude()))
                .andExpect(jsonPath("latitude").value(position.getLatitude()))
                .andExpect(jsonPath("ignition").value(position.getIgnition()));

    }

    @Test
    @DisplayName("Should throw validation error")
    void createInvalidPositionTest() {


    }

}
