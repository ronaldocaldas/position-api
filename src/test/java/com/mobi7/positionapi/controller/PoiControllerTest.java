package com.mobi7.positionapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobi7.positionapi.control.PoiController;
import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.service.PoiService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoiController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PoiControllerTest {

    @MockBean
    private PoiService serviceMock;

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


    private PoiRequest createRandomPoiRequest() {
        return generator.nextObject(PoiRequest.class);
    }

    private Poi createRandomPoi() {
        return generator.nextObject(Poi.class);
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException, JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @DisplayName("PointsInterestController")
    @Nested
    class PointsInterest {

        @Test
        @DisplayName("Should create points of interest with success")
        void createPoiTest() throws Exception {
            // Given
            PoiRequest poiRequest = new PoiRequest();

            // Then
            mockMvc.perform(post("/poi")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(convertObjectToJson(poiRequest))
                            .characterEncoding("UTF-8"))
                    .andExpect(status().isBadRequest());

        }
    }
}
