package com.mobi7.positionapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Position extends PositionBody {

    @Id
    private String positionId;


    /**
     * Build model id
     */
    public void buildId() {
        this.positionId = UUID.randomUUID()
                .toString();
    }



}
