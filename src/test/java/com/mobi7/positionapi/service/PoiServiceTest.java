package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.repository.PoiRepository;
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
public class PoiServiceTest {


    @Autowired
    private PoiService poiServiceMock;

    @MockBean
    private PoiRepository repositoryMock;

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
        PoiRequest request = generator.nextObject(PoiRequest.class);
        when(repositoryMock.save(any(Poi.class))).then(AdditionalAnswers.returnsFirstArg());

        // When
        Poi poiSaved = poiServiceMock.create(request);

        // Then
        assertThat(poiSaved.getPoiId()).isNotNull();
        assertThat(poiSaved.getName()).isEqualTo(request.getName());
        assertThat(poiSaved.getRadius()).isEqualTo(request.getRadius());
        assertThat(poiSaved.getLongitude()).isEqualTo(request.getLongitude());
        assertThat(poiSaved.getLatitude()).isEqualTo(request.getLatitude());
        verify(repositoryMock, times(1)).save(any(Poi.class));
    }

}
