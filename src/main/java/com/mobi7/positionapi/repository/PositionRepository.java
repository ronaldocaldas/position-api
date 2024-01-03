package com.mobi7.positionapi.repository;

import com.mobi7.positionapi.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, String> {
}
