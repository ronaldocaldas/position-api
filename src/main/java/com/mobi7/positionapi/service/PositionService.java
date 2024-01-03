package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PositionService {

    Position create(PositionRequest request) throws Exception;

    List<PositionRequest> parseCSV(MultipartFile file) throws IOException;

    List<Position> getAllPositions();
}
