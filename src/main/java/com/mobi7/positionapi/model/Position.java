package com.mobi7.positionapi.model;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Position {

    @Id
    private String positionId;

    private String plate;

    private Instant datePosition;

    private Integer speed;

    private double latitude;
    private double longitude;

    private Boolean ignition;

    /**
     * Build model id
     */
    public void buildId() {
        this.positionId = UUID.randomUUID()
                .toString();
    }



}
