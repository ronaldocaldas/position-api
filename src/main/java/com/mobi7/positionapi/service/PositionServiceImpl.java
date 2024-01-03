package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.repository.PositionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
}
