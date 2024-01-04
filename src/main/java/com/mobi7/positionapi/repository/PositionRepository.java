package com.mobi7.positionapi.repository;

import com.mobi7.positionapi.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {
    List<Position> findByPlate(String plate);

    @Query(value = "SELECT * FROM position p WHERE CAST(p.date_position AS date) = :datePosition", nativeQuery = true)
    List<Position> findByDatePosition(@Param("datePosition") LocalDate datePosition);
    @Query("SELECT p FROM Position p WHERE p.plate = :plate AND CAST(p.datePosition AS date) = :datePosition")
    List<Position> findByPlateAndDatePosition(@Param("plate") String plate, @Param("datePosition") LocalDate datePosition);}
