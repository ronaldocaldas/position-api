package com.mobi7.positionapi.service;


import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.repository.PositionRepository;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("unit-test")
@SpringBootTest
@AutoConfigureMockMvc
public class PositionServiceTest {

    @Autowired
    PositionService service;

    @MockBean
    private PositionRepository repositoryMock;

    private static EasyRandom generator;


    @BeforeEach
    public void beforeEach() {
        reset(repositoryMock);
    }

    @BeforeAll
    public static void beforeAll() {

        // Random feature generator
        EasyRandomParameters parameters = new EasyRandomParameters();

        // Limit memory (important!)
        parameters.objectPoolSize(3);
        parameters.collectionSizeRange(1, 2);


        generator = new EasyRandom(parameters);

    }

    @Test
    @DisplayName("Should save a position")
    void savePositionTest() throws Exception {
        // fixtures
        Position positionSaved;
        PositionRequest request = generator.nextObject(PositionRequest.class);

        // Mocks
        when(repositoryMock.save(any(Position.class))).then(AdditionalAnswers.returnsFirstArg());

        // Test
        positionSaved = service.create(request);

        // Assertions
        assertThat(positionSaved.getPositionId()).isNotNull();
        assertThat(positionSaved.getDatePosition(), equalTo(request.getDatePosition()));
        assertThat(positionSaved.getPlate(), equalTo(request.getPlate()));
        assertThat(positionSaved.getLongitude(), equalTo(request.getLongitude()));
        assertThat(positionSaved.getLatitude(), equalTo(request.getLatitude()));
        assertThat(positionSaved.getSpeed(), equalTo(request.getSpeed()));
        assertThat(positionSaved.getIgnition(), equalTo(request.getIgnition()));

        verify(repositoryMock, times(1)).save(any(Position.class));

    }
}
