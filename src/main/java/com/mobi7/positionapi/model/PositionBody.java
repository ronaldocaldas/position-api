package com.mobi7.positionapi.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class PositionBody {

    @NotEmpty
    private String plate;
    @NotNull
    private Instant datePosition;
    @NotNull
    private Integer speed;
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;
    @NotNull
    private Boolean ignition;
}
