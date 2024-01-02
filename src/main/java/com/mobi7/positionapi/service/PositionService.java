package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;

public interface PositionService {

    Position create(PositionRequest request) throws Exception;


}
