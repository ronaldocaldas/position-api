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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

            // Skip the first line (header)
            br.readLine();

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

        String[] values = line.split(",");

        // Map CSV values to PositionRequest fields
        String plate = values[0].trim();
        Instant datePosition = parseStringToInstant(values[1].trim()); // Assuming date is in ISO format
        int speed = Integer.parseInt(values[2].trim());
        double longitude = Double.parseDouble(values[3].trim());
        double latitude = Double.parseDouble(values[4].trim());
        boolean ignition = Boolean.parseBoolean(values[5].trim());

        return new PositionRequest(plate, datePosition, speed, latitude, longitude, ignition);
    }

    private Instant parseStringToInstant(String dateString) {
        // Define a custom date format
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'")
                .appendOffset("+HHMM", "+0000")
                .appendLiteral(" (Hora oficial do Brasil)")
                .toFormatter(Locale.US);


        // Parse the string into an Instant
        return  Instant.from(formatter.parse(dateString));

    }
}
