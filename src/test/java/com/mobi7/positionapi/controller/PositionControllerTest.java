package com.mobi7.positionapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobi7.positionapi.control.PositionController;
import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.service.PositionService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
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

@WebMvcTest(PositionController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PositionControllerTest {

    @MockBean
    private PositionService serviceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static EasyRandom generator;

    @Value("classpath:data/positions.csv")
    private Resource positionsCsv;


    @BeforeEach
    void setUp() {
        reset(serviceMock);
    }

    @BeforeAll
    static void beforeAll() {
        EasyRandomParameters parameters = new EasyRandomParameters().collectionSizeRange(2, 6);
        generator = new EasyRandom(parameters);
    }

    private PositionRequest createRandomPositionRequest() {
        return generator.nextObject(PositionRequest.class);
    }

    private Position createRandomPosition() { return generator.nextObject(Position.class);}

    private String convertObjectToJson(Object object) throws JsonProcessingException, JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    @DisplayName("Should create position on H2 database with success")
    void createPositionTest() throws Exception {
        // Given
        PositionRequest request = createRandomPositionRequest();
        ModelMapper modelMapper = new ModelMapper();
        Position position = modelMapper.map(request, Position.class);
        position.buildId();

        // When
        when(serviceMock.create(any(PositionRequest.class))).thenReturn(position);

        // Then
        mockMvc.perform(post("/position/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJson(request))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("positionId").exists())
                .andExpect(jsonPath("plate").value(position.getPlate()))
                .andExpect(jsonPath("datePosition").value(position.getDatePosition().toString()))
                .andExpect(jsonPath("speed").value(position.getSpeed()))
                .andExpect(jsonPath("longitude").value(position.getLongitude()))
                .andExpect(jsonPath("latitude").value(position.getLatitude()))
                .andExpect(jsonPath("ignition").value(position.getIgnition()));
    }

    @Test
    @DisplayName("Should throw validation error")
    void createInvalidPositionTest() throws Exception {
        // Given
        PositionRequest request = new PositionRequest();

        // Then
        mockMvc.perform(post("/position/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJson(request))
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Should retrieve all positions with success")
    void getAllPositionsTest() throws Exception {
        // Given
        List<Position> positions = Arrays.asList(
                createRandomPosition(),
                createRandomPosition(),
                createRandomPosition()
        );

        // When
        when(serviceMock.getAllPositions()).thenReturn(positions);

        // Then
        mockMvc.perform(get("/position/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // Assuming there are 3 positions in the list
                .andExpect(jsonPath("[0].positionId").exists())
                .andExpect(jsonPath("[0].plate").value(positions.get(0).getPlate()))
                .andExpect(jsonPath("[0].datePosition").value(positions.get(0).getDatePosition().toString()))
                .andExpect(jsonPath("[0].speed").value(positions.get(0).getSpeed()))
                .andExpect(jsonPath("[0].longitude").value(positions.get(0).getLongitude()))
                .andExpect(jsonPath("[0].latitude").value(positions.get(0).getLatitude()))
                .andExpect(jsonPath("[0].ignition").value(positions.get(0).getIgnition()));
    }


    @DisplayName("ImportController")
    @Nested
    class ImportFile {
        @Test
        @DisplayName("Should import positions from CSV file")
        void importPositionsTest() throws Exception {
            // Given
            InputStream inputStream = positionsCsv.getInputStream();
            String csvData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            MockMultipartFile file = new MockMultipartFile("file", "positions.csv", "text/csv", csvData.getBytes());

            PositionRequest request1 = createRandomPositionRequest();
            PositionRequest request2 = createRandomPositionRequest();

            List<PositionRequest> positionRequests = Arrays.asList(request1, request2);

            Position position1 = new Position(UUID.randomUUID()
                    .toString(), request1.getPlate(), request1.getDatePosition(), request1.getSpeed(),
                    request1.getLongitude(), request1.getLatitude(), request1.getIgnition());

            Position position2 = new Position(UUID.randomUUID()
                    .toString(), request2.getPlate(), request2.getDatePosition(), request2.getSpeed(),
                    request2.getLongitude(), request2.getLatitude(), request2.getIgnition());

            List<Position> positions = Arrays.asList(position1, position2);

            // When
            when(serviceMock.parseCSV(any(MultipartFile.class))).thenReturn(positionRequests);
            when(serviceMock.create(any(PositionRequest.class))).thenReturn(position1, position2);

            // Then
            mockMvc.perform(MockMvcRequestBuilders.multipart("/position/importPositions")
                            .file(file)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].positionId").exists())
                    .andExpect(jsonPath("$[0].plate").value(positions.get(0).getPlate()))
                    .andExpect(jsonPath("$[0].datePosition").value(positions.get(0).getDatePosition().toString()))
                    .andExpect(jsonPath("$[0].speed").value(positions.get(0).getSpeed()))
                    .andExpect(jsonPath("$[0].longitude").value(positions.get(0).getLongitude()))
                    .andExpect(jsonPath("$[0].latitude").value(positions.get(0).getLatitude()))
                    .andExpect(jsonPath("$[0].ignition").value(positions.get(0).getIgnition()))
                    .andExpect(jsonPath("$[1].positionId").exists())
                    .andExpect(jsonPath("$[1].plate").value(positions.get(1).getPlate()))
                    .andExpect(jsonPath("$[1].datePosition").value(positions.get(1).getDatePosition().toString()))
                    .andExpect(jsonPath("$[1].speed").value(positions.get(1).getSpeed()))
                    .andExpect(jsonPath("$[1].longitude").value(positions.get(1).getLongitude()))
                    .andExpect(jsonPath("$[1].latitude").value(positions.get(1).getLatitude()))
                    .andExpect(jsonPath("$[1].ignition").value(positions.get(1).getIgnition()));
        }


        @Test
        @DisplayName("Should handle exception during importPositions")
        void handleExceptionDuringImportPositionsTest() throws Exception {
            // Given
            MultipartFile mockFile = mock(MultipartFile.class);

            // Mocking service.parseCSV to throw an exception
            when(serviceMock.parseCSV(any(MultipartFile.class)))
                    .thenThrow(new RuntimeException("Simulated exception"));

            // When and Then
            mockMvc.perform(MockMvcRequestBuilders.multipart("/position/importPositions")
                            .file("file", mockFile.getBytes())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isInternalServerError());

            // Verify that service.parseCSV was called
            verify(serviceMock, times(1)).parseCSV(any(MultipartFile.class));
        }
    }

}
