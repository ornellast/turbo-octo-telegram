package com.dnws.wakandaspaceagencyservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ZoneTest {
    @Test
    void should_throwException_when_TlOrBrValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone(null, new Coordinates(1.0, 1.0));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone(new Coordinates(1.0, 1.0), null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone(null, null);
        });
    }
}