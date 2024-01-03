package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.repository.PositionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service("positionService")
public class PositionServiceImpl implements PositionService {

    private final ModelMapper modelMapper;
    private PositionRepository repository;


    public PositionServiceImpl(PositionRepository repository,  ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }
    @Override
    public Position create(PositionRequest request) throws Exception {

        Position position = modelMapper.map(request, Position.class);
        position.buildId();

        return repository.save(position);
    }


    public List<PositionRequest> parseCSV(MultipartFile file) throws IOException {
        List<PositionRequest> positionRequests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parse each line and create PositionRequest objects
                PositionRequest positionRequest = parseLine(line);
                positionRequests.add(positionRequest);
            }
        }

        return positionRequests;
    }

    private PositionRequest parseLine(String line) {
        // Implement logic to parse a CSV line into a PositionRequest object
        // You can use a CSV parsing library or split the line manually
        // For simplicity, I assume the line has comma-separated values
        String[] values = line.split(",");

        // Map CSV values to PositionRequest fields
        String plate = values[0].trim();
        Instant datePosition = Instant.parse(values[1].trim()); // Assuming date is in ISO format
        int speed = Integer.parseInt(values[2].trim());
        double longitude = Double.parseDouble(values[3].trim());
        double latitude = Double.parseDouble(values[4].trim());
        boolean ignition = Boolean.parseBoolean(values[5].trim());

        return new PositionRequest(plate, datePosition, speed, latitude, longitude, ignition);
    }
}
