package com.mobi7.positionapi.control;

import com.mobi7.positionapi.model.Poi;
import com.mobi7.positionapi.model.PoiRequest;
import com.mobi7.positionapi.model.PoiResponse;
import com.mobi7.positionapi.service.PoiService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class PoiController {


    private final PoiService poiService;

    public PoiController(PoiService service) {
        this.poiService = service;
    }

    @PostMapping("poi/poi")
    public ResponseEntity<Poi> doPost(@RequestBody @Valid PoiRequest poiRequest) {
        Poi model = poiService.create(poiRequest);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @GetMapping("/poi/pois")
    public ResponseEntity<List<Poi>> getAllPois() {
        List<Poi> pois = poiService.getAllPois();
        return new ResponseEntity<>(pois, HttpStatus.OK);
    }

    @GetMapping("/poi/response")
    public ResponseEntity<List<PoiResponse>> getFilteredPositions(
            @RequestParam(required = false) String plate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate datePosition) {

        List<PoiResponse> filteredPositions = poiService.getPoiResponses(plate, datePosition);
        return new ResponseEntity<>(filteredPositions, HttpStatus.OK);
    }
    @PostMapping("/poi/import")
    public ResponseEntity<List<Poi>> importPositions(@RequestParam("file") MultipartFile file) {
        try {
            List<PoiRequest> poiRequests = poiService.parseCSV(file);
            List<Poi> pois = new ArrayList<>();

            for (PoiRequest poiRequest : poiRequests) {
                Poi model = poiService.create(poiRequest);
                pois.add(model);
            }

            return new ResponseEntity<>(pois, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
