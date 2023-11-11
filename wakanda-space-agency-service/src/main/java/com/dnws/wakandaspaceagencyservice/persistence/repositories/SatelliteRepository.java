package com.dnws.wakandaspaceagencyservice.persistence.repositories;

import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SatelliteRepository extends JpaRepository<SatelliteEntity, UUID> {
}
