package com.dnws.wakandaspaceagencyservice.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @ParameterizedTest
    @CsvSource(value = {
            "1.1, ",
            " , 2.2",
            " , "
    })
    void should_throwException_when_LatOrLngIsNull(
            Double lat,
            Double lon
    ){
        assertThrows(IllegalArgumentException.class, () ->{
            new Coordinate(lat, lon);
        });
    }
}