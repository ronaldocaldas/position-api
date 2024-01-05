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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoiController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PoiControllerTest {

    @MockBean
    private PoiService poiServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static EasyRandom generator;

    @Value("classpath:data/pois.csv")
    private Resource poisCsv;


    @BeforeEach
    void setUp() {
        reset(poiServiceMock);
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
            PoiRequest poiRequest = createRandomPoiRequest();
            Poi poi = createRandomPoi();

            // When
            when(poiServiceMock.create(any(PoiRequest.class))).thenReturn(poi);

            // Then
            mockMvc.perform(post("/poi/poi")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(convertObjectToJson(poiRequest))
                            .characterEncoding("UTF-8"))
                    .andExpect(status().isCreated());

        }


    }

    @DisplayName("ImportController")
    @Nested
    class ImportFile {
        @Test
        @DisplayName("Should import POI from CSV file")
        void importPoiTest() throws Exception {
            // Given
            InputStream inputStream = poisCsv.getInputStream();
            String csvData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            MockMultipartFile file = new MockMultipartFile("file", "pois.csv", "text/csv", csvData.getBytes());

            PoiRequest request1 = createRandomPoiRequest();
            PoiRequest request2 = createRandomPoiRequest();

            List<PoiRequest> poisRequests = Arrays.asList(request1, request2);

            Poi poi1 = new Poi(UUID.randomUUID()
                    .toString(), request1.getName(), request1.getRadius(),
                    request1.getLongitude(), request1.getLatitude());

            Poi poi2 = new Poi(UUID.randomUUID()
                    .toString(), request2.getName(), request2.getRadius(),
                    request2.getLongitude(), request2.getLatitude());


            List<Poi> pois = Arrays.asList(poi1, poi2);

            // When
            when(poiServiceMock.parseCSV(any(MultipartFile.class))).thenReturn(poisRequests);
            when(poiServiceMock.create(any(PoiRequest.class))).thenReturn(poi1, poi2);

            // Then
            mockMvc.perform(MockMvcRequestBuilders.multipart("/poi/import")
                            .file(file)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].poiId").exists())
                    .andExpect(jsonPath("$[0].name").value(pois.get(0).getName()))
                    .andExpect(jsonPath("$[0].radius").value(pois.get(0).getRadius()))
                    .andExpect(jsonPath("$[0].longitude").value(pois.get(0).getLongitude()))
                    .andExpect(jsonPath("$[0].latitude").value(pois.get(0).getLatitude()))

                    .andExpect(jsonPath("$[1].poiId").exists())
                    .andExpect(jsonPath("$[1].name").value(pois.get(1).getName()))
                    .andExpect(jsonPath("$[1].radius").value(pois.get(1).getRadius()))
                    .andExpect(jsonPath("$[1].longitude").value(pois.get(1).getLongitude()))
                    .andExpect(jsonPath("$[1].latitude").value(pois.get(1).getLatitude()));
        }

        @DisplayName("PointsOfInterestController")
        @Nested
        class PointsOfInterest {

            @Test
            @DisplayName("Should get all points of interest with success")
            void getAllPoisTest() throws Exception {
                // Given
                List<Poi> pois = Arrays.asList(createRandomPoi(), createRandomPoi());

                // When
                when(poiServiceMock.getAllPois()).thenReturn(pois);

                // Then
                mockMvc.perform(get("/poi/pois")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2))) // Adjust based on the expected size of your list
                        .andExpect(jsonPath("$[0].poiId").value(pois.get(0).getPoiId()))
                        .andExpect(jsonPath("$[1].poiId").value(pois.get(1).getPoiId()));
            }
        }

        @Test
        @DisplayName("Should handle exception during import pois")
        void handleExceptionDuringImportPoisTest() throws Exception {
            // Given
            MultipartFile mockFile = mock(MultipartFile.class);

            // Mocking service.parseCSV to throw an exception
            when(poiServiceMock.parseCSV(any(MultipartFile.class)))
                    .thenThrow(new RuntimeException("Simulated exception"));

            // When and Then
            mockMvc.perform(MockMvcRequestBuilders.multipart("/poi/import")
                            .file("file", mockFile.getBytes())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isInternalServerError());

            // Verify that service.parseCSV was called
            verify(poiServiceMock, times(1)).parseCSV(any(MultipartFile.class));
        }
    }
}
