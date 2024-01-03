package com.mobi7.positionapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "position")
public class Position {

    @Id
    @Column
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
