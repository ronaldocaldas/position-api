package com.mobi7.positionapi.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "poi")
public class Poi {

    @Id
    @Column
    private String poiId;

    private String name;
    private double radius;
    private double latitude;
    private double longitude;

    /**
     * Build model id
     */
    public void buildId() {
        this.poiId = UUID.randomUUID()
                .toString();
    }

}
