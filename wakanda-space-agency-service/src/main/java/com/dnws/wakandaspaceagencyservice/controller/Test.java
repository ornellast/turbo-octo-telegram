package com.dnws.wakandaspaceagencyservice.controller;

import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinates;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(path = "/test")
public class Test {

    private SatelliteRepository repository;

    public Test(SatelliteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<SatelliteEntity>> listAll() {
        return ResponseEntity.ok(repository.findAll());

    }

    @PostMapping
    public ResponseEntity<SatelliteEntity> SaveDummy() {
        var entity = new SatelliteEntity();
        entity.setReadingFrequency(new Frequency(TimeUnit.MINUTES, 5));
        entity.setActive(true);
        entity.setType(SatelliteType.WEATHER);
        entity.setZones(
                List.of(
                        new Zone(
                                new Coordinates(Math.random() * 10, Math.random() * 100),
                                new Coordinates(Math.random() * 10, Math.random() * 100)
                        )
                )
        );
        return ResponseEntity.ok(repository.save(entity));

    }
}
