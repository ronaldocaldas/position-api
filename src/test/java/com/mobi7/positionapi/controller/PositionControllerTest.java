package com.mobi7.positionapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobi7.positionapi.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

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

    @Autowired
    private ObjectMapper jsonMapper;
    private static EasyRandom generator;


    @BeforeEach
    void before() {
        // Reset mocks
    }

    private Position randomPosition() {
        return generator.nextObject(Position.class);
    }


    @Test
    @DisplayName("Should create position on H2 database with success")
    void createPositionTest() throws Exception {

        // Test
        mockMvc.perform(post("/position")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plate").exists())
                .andExpect(jsonPath("$.datePosition").exists())
                .andExpect(jsonPath("$.speed").exists())
                .andExpect(jsonPath("$.longitude").exists())
                .andExpect(jsonPath("$.latityde").exists());

    }

    @Test
    @DisplayName("Should throw validation error")
    void createInvalidPositionTest(){


    }

}
