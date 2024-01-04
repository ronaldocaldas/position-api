package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PoiService {
    Poi create(PoiRequest poiRequest);

    List<PoiRequest> parseCSV(MultipartFile file) throws IOException;
}
