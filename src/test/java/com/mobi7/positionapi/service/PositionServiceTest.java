package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.repository.PositionRepository;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.*;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit-test")
@SpringBootTest
@AutoConfigureMockMvc
public class PositionServiceTest {

    @Autowired
    private PositionService positionServiceMock;

    @MockBean
    private PositionRepository repositoryMock;

    private static EasyRandom generator;

    @Captor
    private ArgumentCaptor<String> plateCaptor;

    @Captor
    private ArgumentCaptor<LocalDate> localDateCaptor;

    @Captor
    private ArgumentCaptor<Instant> instantCaptor;


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

    @DisplayName("CreateService")
    @Nested
    class CrudCreate {

        @Test
        @DisplayName("Should save a position")
        void savePositionTest() throws Exception {
            // Given
            PositionRequest request = generator.nextObject(PositionRequest.class);
            when(repositoryMock.save(any(Position.class))).then(AdditionalAnswers.returnsFirstArg());

            // When
            Position positionSaved = positionServiceMock.create(request);

            // Then
            assertPositionFields(positionSaved, request);
            verify(repositoryMock, times(1)).save(any(Position.class));
        }


        @Test
        @DisplayName("Should retrieve all positions")
        void getAllPositionsTest() {
            // Given
            List<Position> positions = Arrays.asList(
                    generator.nextObject(Position.class),
                    generator.nextObject(Position.class),
                    generator.nextObject(Position.class)
            );

            when(repositoryMock.findAll()).thenReturn(positions);

            // When
            List<Position> retrievedPositions = positionServiceMock.getAllPositions();

            // Then
            assertEquals(positions.size(), retrievedPositions.size());
            assertThat(retrievedPositions).isEqualTo(positions);

            verify(repositoryMock, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return positions filtered")
        public void testGetFilteredPositions() {
            // Mock data
            Position position1 = Position.builder().speed(100).plate("ABC123").positionId("any").datePosition(Instant.parse("2018-12-12T02:04:03Z")).build();
            Position position2 = Position.builder().speed(100).plate("ABC123").positionId("any").datePosition(Instant.parse("2018-12-13T02:04:03Z")).build();
            Position position3 = Position.builder().speed(100).plate("DEF456").positionId("any").datePosition(Instant.parse("2018-12-12T02:04:03Z")).build();
            Position position4 = Position.builder().speed(100).plate("DEF456").positionId("any").datePosition(Instant.parse("2018-12-13T02:04:03Z")).build();

            // Mocking repository behavior
            when(repositoryMock.findByPlateAndDatePosition("ABC123", LocalDate.of(2018, 12, 12))).thenReturn(Arrays.asList(position1, position2));
            when(repositoryMock.findByPlate("ABC123")).thenReturn(Arrays.asList(position1, position2));

            // Convert Instant to LocalDate before passing to findByDatePosition
            LocalDate localDateForDatePosition = Instant.parse("2018-12-13T02:04:03Z")
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            when(repositoryMock.findByDatePosition(localDateForDatePosition)).thenReturn(Arrays.asList(position2, position4));

            when(repositoryMock.findAll()).thenReturn(Arrays.asList(position1, position2, position3, position4));

            // Test case 1: Both plate and datePosition provided
            List<Position> result1 = positionServiceMock.getFilteredPositions("ABC123", LocalDate.of(2018, 12, 12));
            verify(repositoryMock).findByPlateAndDatePosition(plateCaptor.capture(), localDateCaptor.capture());
            assertThat(plateCaptor.getValue()).isEqualTo("ABC123");
            assertThat(localDateCaptor.getValue()).isEqualTo(LocalDate.of(2018, 12, 12));
            assertThat(result1).isEqualTo(Arrays.asList(position1, position2));

            // Test case 2: Only plate provided
            List<Position> result2 = positionServiceMock.getFilteredPositions("ABC123", null);
            verify(repositoryMock).findByPlate(plateCaptor.capture());
            assertThat(plateCaptor.getValue()).isEqualTo("ABC123");
            assertThat(result2).isEqualTo(Arrays.asList(position1, position2));

            // Test case 3: Only datePosition provided
            List<Position> result3 = positionServiceMock.getFilteredPositions(null, LocalDate.of(2018, 12, 13));
            verify(repositoryMock).findByDatePosition(localDateCaptor.capture());
            assertThat(localDateCaptor.getValue()).isEqualTo(LocalDate.of(2018, 12, 13));
            assertThat(result3).isEqualTo(Arrays.asList(position2, position4));

            // Test case 4: No filters provided, return all positions
            List<Position> result4 = positionServiceMock.getFilteredPositions(null, null);
            verify(repositoryMock).findAll();
            assertThat(result4).isEqualTo(Arrays.asList(position1, position2, position3, position4));
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

    @DisplayName("ImportService")
    @Nested
    class ImportFile {
        @Test
        @DisplayName("Should parse CSV file")
        void parseCSVTest() throws IOException {
            // Given
            MultipartFile file = createMockMultipartFile("plate,date,speed,longitude,latitude,ignition\n" +
                    "ABC123,Wed Dec 12 2018 00:04:03 GMT-0200 (Hora oficial do Brasil),60,10.0,20.0,true");

            // When
            List<PositionRequest> positionRequests = positionServiceMock.parseCSV(file);

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
