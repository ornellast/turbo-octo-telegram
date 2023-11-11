package com.dnws.wakandaspaceagencyservice.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ScannedZoneTest {
    @Test
    void should_throwException_when_TlOrBrValueIsNull(){
        assertThrows(IllegalArgumentException.class, () ->{
            new ScannedZone(null,new Coordinates(1.0, 1.0));
        });
        assertThrows(IllegalArgumentException.class, () ->{
            new ScannedZone(new Coordinates(1.0, 1.0), null);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            new ScannedZone(null, null);
        });
    }
}