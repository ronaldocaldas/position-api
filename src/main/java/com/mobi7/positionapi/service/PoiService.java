package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;

public interface PoiService {
    Poi createPoi(PoiRequest poiRequest);
}
