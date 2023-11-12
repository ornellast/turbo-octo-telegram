package com.dnws.wakandaspaceagencyservice.controller;

import com.dnws.wakandaspaceagencyservice.TestUtils;
import com.dnws.wakandaspaceagencyservice.model.Satellite;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SatelliteControllerIT {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private String getSatelliteUrl(UUID satelliteId, String path) {
        String baseUrl = "http://localhost:" + port + "/satellite";

        if (satelliteId == null) {
            return baseUrl;
        }
        String withSatelliteId = baseUrl + satelliteId + "/";
        if (path == null) {
            return withSatelliteId;
        }

        return withSatelliteId + path + "/";
    }

    private ResponseEntity<Satellite> createNewSatellite(){
        var satellite = Satellite.from(TestUtils.createEntity(null));

        // When
        return restTemplate.postForEntity(
                getSatelliteUrl(null, null),
                satellite,
                Satellite.class
        );
    }

    @Test
    void createSatellite_shouldReturnTheNewlyCreateSatellite() {
        // Given
        var satellite = Satellite.from(TestUtils.createEntity(null));

        // When
        ResponseEntity<Satellite> responseEntity = restTemplate.postForEntity(
                getSatelliteUrl(null, null),
                satellite,
                Satellite.class
        );

        // Then
        Satellite responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.id());
        assertEquals(satellite.zones().size(), responseBody.zones().size());
        assertNotNull(responseBody.zones().get(0).id());


    }
}