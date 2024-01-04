package com.mobi7.positionapi.service;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.repository.PoiRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service("poiService")
public class PoiServiceImpl implements  PoiService{
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
}
