package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.repository.PositionRepository;
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
import java.time.Instant;
import java.util.List;

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

    @DisplayName("Create")
    @Nested
    class CrudCreate {

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

    @DisplayName("Import")
    @Nested
    class ImportFile {
        @Test
        @DisplayName("Should parse CSV file")
        void parseCSVTest() throws IOException {
            // Given
            MultipartFile file = createMockMultipartFile("plate,date,speed,longitude,latitude,ignition\n" +
                    "ABC123,Wed Dec 12 2018 00:04:03 GMT-0200 (Hora oficial do Brasil),60,10.0,20.0,true");

            // When
            List<PositionRequest> positionRequests = service.parseCSV(file);

            // Then
            assertThat(positionRequests).hasSize(1);
            PositionRequest request = positionRequests.get(0);
            assertThat(request.getPlate()).isEqualTo("ABC123");
            assertThat(request.getDatePosition()).isEqualTo(Instant.parse("2018-12-12T02:04:03Z"));
            assertThat(request.getSpeed()).isEqualTo(60);
            assertThat(request.getLongitude()).isEqualTo(10.0);
            assertThat(request.getLatitude()).isEqualTo(20.0);
            assertThat(request.getIgnition()).isTrue();
        }

        private MultipartFile createMockMultipartFile(String content) throws IOException {
            return new MockMultipartFile("file.csv", content.getBytes());
        }
    }
}
