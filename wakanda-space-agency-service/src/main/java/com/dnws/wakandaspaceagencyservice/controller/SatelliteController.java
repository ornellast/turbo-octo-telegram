package com.dnws.wakandaspaceagencyservice.controller;

import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Satellite;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.service.ISatelliteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping(path = "/satellite")
public class SatelliteController {

    private final ISatelliteService service;

    public SatelliteController(ISatelliteService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<Satellite>> listAll() {
        return ResponseEntity.ok(
                service.findAll()
                        .stream()
                        .map(Satellite::from)
                        .toList()
        );
    }

    @GetMapping(path = "/{satelliteId}")
    public ResponseEntity<Satellite> get(
            @PathVariable UUID satelliteId
    ) {
        return service.findById(satelliteId).map(entity -> ResponseEntity.ok(Satellite.from(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{satelliteId}/status")
    public ResponseEntity<String> getStatus(
            @PathVariable UUID satelliteId
    ) {
        return service.findById(satelliteId).map(entity -> {
                    if (entity.isActive()) {
                        return ResponseEntity.ok("alive");
                    }
                    return ResponseEntity.ok("hibernating");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/{satelliteId}/activate")
    public ResponseEntity<String> setStatusActive(
            @PathVariable UUID satelliteId
    ) {
        if(!service.activate(satelliteId)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{satelliteId}/inactivate")
    public ResponseEntity<String> setStatusInactive(
            @PathVariable UUID satelliteId
    ) {
        if(!service.deactivate(satelliteId)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{satelliteId}/readingFrequency")
    public ResponseEntity<?> updateReadingFrequency(
            @PathVariable UUID satelliteId,
            @RequestBody Frequency newFrequency
    ) {

        return service.findById(satelliteId).map(entity -> {
            entity.setReadingFrequency(newFrequency);
            if (!service.updateReadingFrequency(satelliteId, newFrequency)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<Satellite> createSatellite(
            @RequestBody Satellite satellite
    ) {
        Optional<SatelliteEntity> optional = service.save(satellite.toEntity());

        return optional.map(entity -> ResponseEntity.ok(Satellite.from(entity)
        )).orElseGet(() -> ResponseEntity.internalServerError().build());

    }

    @DeleteMapping(path = "/{satelliteId}")
    public ResponseEntity<?> decommission(
            @PathVariable UUID satelliteId
    ) {
        if (!service.decommission(satelliteId)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }
}
