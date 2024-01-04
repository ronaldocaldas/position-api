package com.mobi7.positionapi.control;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.service.PoiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class PoiController {


    private final PoiService poiService;

    public PoiController(PoiService service) {
        this.poiService = service;
    }

    @PostMapping("/poi")
    public ResponseEntity<Poi> doPost(@RequestBody @Valid PoiRequest poiRequest)
            throws Exception {
        Poi model = poiService.create(poiRequest);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }
}
