package com.mobi7.positionapi.control;

import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @PostMapping("/position")
    public ResponseEntity<Position> doPost(@RequestBody @Valid PositionRequest positionRequest)
            throws Exception {
        Position model = service.create(positionRequest);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

}
