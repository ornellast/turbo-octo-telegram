package com.dnws.wakandaspaceagencyservice.controller;

import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.service.ISatelliteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(path = "/test")
public class Test {

    private final ISatelliteService service;

    public Test(ISatelliteService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<SatelliteEntity>> listAll() {
        return ResponseEntity.ok(service.findAll());

    }

    @PostMapping
    public ResponseEntity<SatelliteEntity> saveDummy() {
        var entity = new SatelliteEntity();
        entity.setReadingFrequency(new Frequency(TimeUnit.SECONDS, 5L));
        entity.setActive(true);
        entity.setType(SatelliteType.WEATHER);
        entity.setZones(
                List.of(
                        new Zone(
                                new Coordinate(Math.random() * 10, Math.random() * 100),
                                new Coordinate(Math.random() * 10, Math.random() * 100)
                        ),
                        new Zone(
                                new Coordinate(Math.random() * 10, Math.random() * 100),
                                new Coordinate(Math.random() * 10, Math.random() * 100)
                        )
                )
        );


        Optional<SatelliteEntity> optional = service.save(entity);

        if(optional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optional.get());

    }
}
