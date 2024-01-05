package com.mobi7.positionapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoiResponse {

    private String poiId;
    private String poiName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String plate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate datePosition;
    private String timeSpend;
}
