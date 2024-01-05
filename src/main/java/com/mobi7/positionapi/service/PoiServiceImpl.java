package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.repository.PoiRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Service("poiService")
public class PoiServiceImpl implements  PoiService {
    private final ModelMapper modelMapper;

    private final PoiRepository repository;

    public PoiServiceImpl(ModelMapper modelMapper, PoiRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
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

