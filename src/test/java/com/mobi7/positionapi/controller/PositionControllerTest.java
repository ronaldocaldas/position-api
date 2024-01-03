package com.mobi7.positionapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobi7.positionapi.control.PositionController;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PositionController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PositionControllerTest {

    @MockBean
    private PositionService serviceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static EasyRandom generator;

    @BeforeEach
    void setUp() {
        reset(serviceMock);
    }

    @BeforeAll
    static void beforeAll() {
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(2, 6);
        generator = new EasyRandom(parameters);
    }

    private PositionRequest createRandomPositionRequest() {
        return generator.nextObject(PositionRequest.class);
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException, JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    @DisplayName("Should create position on H2 database with success")
    void createPositionTest() throws Exception {
        // Given
        PositionRequest request = createRandomPositionRequest();
        ModelMapper modelMapper = new ModelMapper();
        Position position = modelMapper.map(request, Position.class);
        position.buildId();

        // When
        when(serviceMock.create(any(PositionRequest.class))).thenReturn(position);

        // Then
        mockMvc.perform(post("/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJson(request))
                        .characterEncoding("UTF-8"))
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
    void createInvalidPositionTest() throws Exception {
        // Given
        PositionRequest request = new PositionRequest();

        // Then
        mockMvc.perform(post("/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJson(request))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
