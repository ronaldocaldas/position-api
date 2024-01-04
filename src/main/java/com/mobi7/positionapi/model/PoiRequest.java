package com.mobi7.positionapi.model;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PoiRequest {

    @NotEmpty
    private String name;
    @NotNull
    private double radius;
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;


}