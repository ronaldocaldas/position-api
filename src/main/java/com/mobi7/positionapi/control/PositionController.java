package com.mobi7.positionapi.control;

import com.mobi7.positionapi.exceptions.ApiErrors;
import com.mobi7.positionapi.model.Position;
import com.mobi7.positionapi.model.PositionRequest;
import com.mobi7.positionapi.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @PostMapping("/position/position")
    public ResponseEntity<Position> doPost(@RequestBody @Valid PositionRequest positionRequest)
            throws Exception {
        Position model = service.create(positionRequest);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @GetMapping("/position/positions")
    public ResponseEntity<List<Position>> getAllPositions() {
        List<Position> positions = service.getAllPositions();
        return new ResponseEntity<>(positions, HttpStatus.OK);
    }

    @PostMapping("/position/import")
    public ResponseEntity<List<Position>> importPositions(@RequestParam("file") MultipartFile file) {
        try {
            List<PositionRequest> positionRequests = service.parseCSV(file);
            List<Position> positions = new ArrayList<>();

            for (PositionRequest positionRequest : positionRequests) {
                Position model = service.create(positionRequest);
                positions.add(model);
            }

            return new ResponseEntity<>(positions, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/position/positions/filter")
    public ResponseEntity<List<Position>> getFilteredPositions(
            @RequestParam(required = false) String plate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate datePosition) {

        List<Position> filteredPositions = service.getFilteredPositions(plate, datePosition);
        return new ResponseEntity<>(filteredPositions, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationErrors(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        return  new ApiErrors(bindingResult);
    }
}
