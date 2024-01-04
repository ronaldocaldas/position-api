package com.mobi7.positionapi.repository;

import com.mobi7.positionapi.model.Poi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoiRepository extends JpaRepository<Poi, String> {
}