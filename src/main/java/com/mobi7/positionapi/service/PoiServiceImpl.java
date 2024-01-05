package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.model.PoiResponse;
import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.repository.PoiRepository;
import com.mobi7.positionapi.utils.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mobi7.positionapi.utils.PositionsUtils.isWithinRadius;


@Service("poiService")
public class PoiServiceImpl implements PoiService {
    private final ModelMapper modelMapper;

    private final PoiRepository repository;

    private final PositionService positionService;

    public PoiServiceImpl(ModelMapper modelMapper, PoiRepository repository, PositionService positionService) {
        this.modelMapper = modelMapper;
        this.repository = repository;
        this.positionService = positionService;
    }

    @Override
    public Poi create(PoiRequest poiRequest) {
        Poi poi = modelMapper.map(poiRequest, Poi.class);
        poi.buildId();
        return repository.save(poi);
    }

    @Override
    public List<PoiRequest> parseCSV(MultipartFile file) throws IOException {
        List<PoiRequest> poiRequests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            // Skip the first line (header)
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                // Parse each line and create PositionRequest objects
                PoiRequest poiRequest = parseLine(line);
                poiRequests.add(poiRequest);
            }
        }

        return poiRequests;
    }

    @Override
    public List<Poi> getAllPois() {
        return repository.findAll();
    }

    @Override
    public List<PoiResponse> getPoiResponses(String plate, LocalDate datePosition) {
        List<Poi> listPois = getAllPois();
        List<Position> listPositions = positionService.getFilteredPositions(plate, datePosition);

        return listPois.stream()
                .flatMap(poi -> {
                    // Map all positions inside the area of Poi
                    List<Position> positionsWithinRadius = listPositions.stream()
                            .filter(position -> isWithinRadius(poi.getLatitude(), poi.getLongitude(),
                                    position.getLatitude(), position.getLongitude(), poi.getRadius()))
                            .collect(Collectors.toList());

                    // For all positions calculate the time
                    if (!positionsWithinRadius.isEmpty()) {
                        String timeSpentFormatted = DateUtils.calculateAndFormatTotalDuration(positionsWithinRadius);
                        return Stream.of(new PoiResponse(poi.getPoiId(), poi.getName(), plate, datePosition, timeSpentFormatted));
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }


    private PoiRequest parseLine(String line) {

        String[] values = line.split(",");

        // Map CSV values to PositionRequest fields
        String name = values[0].trim();
        int radius = Integer.parseInt(values[1].trim());
        double longitude = Double.parseDouble(values[2].trim());
        double latitude = Double.parseDouble(values[3].trim());

        return new PoiRequest(name, radius, latitude, longitude);

    }
}

