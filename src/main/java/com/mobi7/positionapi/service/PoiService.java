package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.model.PoiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface PoiService {
    Poi create(PoiRequest poiRequest);

    List<PoiRequest> parseCSV(MultipartFile file) throws IOException;

    List<Poi> getAllPois();

    List<PoiResponse> getPoiResponses(String plate, LocalDate datePosition);
}

