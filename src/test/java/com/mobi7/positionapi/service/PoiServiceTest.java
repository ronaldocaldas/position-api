package com.mobi7.positionapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.model.PoiResponse;
import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.repository.PoiRepository;
import com.mobi7.positionapi.repository.PositionRepository;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.*;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private PositionService positionServiceMock;

    @MockBean
    private PoiRepository repositoryMock;

    @MockBean
    private PositionRepository positionRepositoryMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:data/pois.json")
    private Resource poisJson;

    @Value("classpath:data/positions.json")
    private Resource positionsJson;

    private static EasyRandom generator;

    @BeforeEach
    public void setUp() {
        reset(repositoryMock, positionServiceMock, positionRepositoryMock);
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


    @DisplayName("PoiService")
    @Nested
    class FilteredPositions {

        private static final String DATE_STRING = "12/12/2018";
        private static final LocalDate DATE_POSITION = LocalDate.parse(DATE_STRING, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        @Test
        @DisplayName("Get filtered positions by plate and date of position")
        void testGetFilteredByPlateAndPositions() throws Exception {
            testFilteredPositions("TESTE001", DATE_POSITION, "[\n" +
                    "    {\n" +
                    "        \"poiId\": \"3a7b116b-1f5a-41af-9994-c78e6d7c2aca\",\n" +
                    "        \"poiName\": \"PONTO 1\",\n" +
                    "        \"plate\": \"TESTE001\",\n" +
                    "        \"datePosition\": \"2018-12-12\",\n" +
                    "        \"timeSpend\": \"0 days 00:30:03\"\n" +
                    "    }\n" +
                    "]");
        }

        @Test
        @DisplayName("Get filtered positions by date of position")
        void testGetFilteredByPositions() throws Exception {
            testFilteredPositions(null, DATE_POSITION, "[\n" +
                    "    {\n" +
                    "        \"poiId\": \"353e9cb9-cbd7-4c43-b8df-eb027cfbea48\",\n" +
                    "        \"poiName\": \"PONTO 1\",\n" +
                    "        \"datePosition\": \"2018-12-12\",\n" +
                    "        \"timeSpend\": \"0 days 02:00:12\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"poiId\": \"873207a-6b50-4a09-815c-45e98e2f8853\",\n" +
                    "        \"poiName\": \"PONTO 2\",\n" +
                    "        \"datePosition\": \"2018-12-12\",\n" +
                    "        \"timeSpend\": \"0 days 02:30:15\"\n" +
                    "    }\n" +
                    "]");
        }

        @Test
        @DisplayName("Get filtered positions by plate")
        void testGetFilteredByPlate() throws Exception {
            testFilteredPositions("TESTE002", null, "[\n" +
                    "    {\n" +
                    "        \"poiId\": \"a2b0dda6-5b61-43d5-98fe-8666149f83af\",\n" +
                    "        \"poiName\": \"PONTO 1\",\n" +
                    "        \"plate\": \"TESTE002\",\n" +
                    "        \"timeSpend\": \"0 days 01:00:06\"\n" +
                    "    }\n" +
                    "]");
        }

        private void testFilteredPositions(String plate, LocalDate datePosition, String resultResponse) throws Exception {
            List<PoiResponse> expectedResponses = loadPoiResponseJsonResource(resultResponse);

            // Load test data from JSON files
            String poiJsonContent = loadJsonResourceAsString(poisJson);
            List<Poi> pois = loadPoiJsonResource(poiJsonContent);
            String positionJsonContent = loadJsonResourceAsString(positionsJson);
            List<Position> positions = loadPositionJsonResource(positionJsonContent);

            // Filter the positions to mock
            List<Position> filteredPositions = positions.stream()
                    .filter(position -> (plate == null || plate.equals(position.getPlate()))
                            && (datePosition == null || datePosition.equals(position.getDatePosition().atZone(ZoneId.systemDefault()).toLocalDate())))
                    .collect(Collectors.toList());

            when(repositoryMock.findAll()).thenReturn(pois);
            when(positionServiceMock.getFilteredPositions(eq(plate), eq(datePosition))).thenReturn(filteredPositions);

            List<PoiResponse> result = poiServiceMock.getPoiResponses(plate, datePosition);

            // Assertions
            assertThat(result).isNotEmpty();
            assertThat(result.size()).isEqualTo(expectedResponses.size());

            for (int i = 0; i < expectedResponses.size(); i++) {
                PoiResponse actualResponse = result.get(i);
                PoiResponse expectedResponse = expectedResponses.get(i);

                assertThat(actualResponse.getPoiName()).isEqualTo(expectedResponse.getPoiName());
                assertThat(actualResponse.getPlate()).isEqualTo(expectedResponse.getPlate());
                assertThat(actualResponse.getDatePosition()).isEqualTo(expectedResponse.getDatePosition());
                assertThat(actualResponse.getTimeSpend()).isEqualTo(expectedResponse.getTimeSpend());
            }

            // Verify that the repositoryMock and positionServiceMock methods were called
            verify(repositoryMock, times(1)).findAll();
            verify(positionServiceMock, times(1)).getFilteredPositions(eq(plate), eq(datePosition));
        }


        private String loadJsonResourceAsString(Resource resource) throws IOException {
            try (InputStream inputStream = resource.getInputStream()) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
        }

        private List<Poi> loadPoiJsonResource(String jsonContent) throws IOException {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Poi.class);
            return objectMapper.readValue(jsonContent, listType);
        }

        private List<Position> loadPositionJsonResource(String jsonContent) throws IOException {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Position.class);
            return objectMapper.readValue(jsonContent, listType);
        }

        private List<PoiResponse> loadPoiResponseJsonResource(String jsonContent) throws IOException {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, PoiResponse.class);
            return objectMapper.readValue(jsonContent, listType);
        }

    }


}
