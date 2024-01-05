package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.repository.PoiRepository;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.*;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("Should get all POIs with success")
    void getAllPoisTest() {
        // Given
        List<Poi> pois = Arrays.asList(createRandomPoi(), createRandomPoi());

        // When
        when(repositoryMock.findAll()).thenReturn(pois);

        // Then
        List<Poi> result = poiServiceMock.getAllPois();
        assertEquals(2, result.size());
        assertEquals(pois.get(0), result.get(0));
        assertEquals(pois.get(1), result.get(1));
    }

    private Poi createRandomPoi() {
        return generator.nextObject(Poi.class);
    }

    @DisplayName("ImportService")
    @Nested
    class ImportFile {
        @Test
        @DisplayName("Should parse CSV file")
        void parseCSVTest() throws IOException {
            // Given
            MultipartFile file = createMockMultipartFile("name,radius,longitude,latitude\n" +
                    "ABC123,60,10.0,20.0");

            // When
            List<PoiRequest> poiRequests = poiServiceMock.parseCSV(file);

            // Then
            assertThat(poiRequests).hasSize(1);
            PoiRequest request = poiRequests.get(0);
            assertThat(request.getName()).isEqualTo("ABC123");
            assertThat(request.getRadius()).isEqualTo(60);
            assertThat(request.getLongitude()).isEqualTo(10.0);
            assertThat(request.getLatitude()).isEqualTo(20.0);
        }
        private MultipartFile createMockMultipartFile(String content) throws IOException {
            return new MockMultipartFile("file.csv", content.getBytes());
        }
    }

}
