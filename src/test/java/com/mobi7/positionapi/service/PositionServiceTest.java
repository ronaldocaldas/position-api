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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit-test")
@SpringBootTest
@AutoConfigureMockMvc
public class PositionServiceTest {

    @Autowired
    private PositionService service;

    @MockBean
    private PositionRepository repositoryMock;

    private static EasyRandom generator;

    @BeforeEach
    public void setUp() {
        reset(repositoryMock);
    }

    @BeforeAll
    public static void beforeAll() {
        setUpRandomGenerator();
    }

    private static void setUpRandomGenerator() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .objectPoolSize(3)
                .collectionSizeRange(1, 2);

        generator = new EasyRandom(parameters);
    }

    @Test
    @DisplayName("Should save a position")
    void savePositionTest() throws Exception {
        // Given
        PositionRequest request = generator.nextObject(PositionRequest.class);
        when(repositoryMock.save(any(Position.class))).then(AdditionalAnswers.returnsFirstArg());

        // When
        Position positionSaved = service.create(request);

        // Then
        assertPositionFields(positionSaved, request);
        verify(repositoryMock, times(1)).save(any(Position.class));
    }

    private void assertPositionFields(Position positionSaved, PositionRequest request) {
        assertThat(positionSaved.getPositionId()).isNotNull();
        assertThat(positionSaved.getDatePosition()).isEqualTo(request.getDatePosition());
        assertThat(positionSaved.getPlate()).isEqualTo(request.getPlate());
        assertThat(positionSaved.getLongitude()).isEqualTo(request.getLongitude());
        assertThat(positionSaved.getLatitude()).isEqualTo(request.getLatitude());
        assertThat(positionSaved.getSpeed()).isEqualTo(request.getSpeed());
        assertThat(positionSaved.getIgnition()).isEqualTo(request.getIgnition());
    }
}
