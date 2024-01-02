package com.mobi7.positionapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class PositionRequest {

    private String plate;

    private Instant datePosition;

    private Integer speed;

    private double latitude;
    private double longitude;

    private Boolean ignition;


}
