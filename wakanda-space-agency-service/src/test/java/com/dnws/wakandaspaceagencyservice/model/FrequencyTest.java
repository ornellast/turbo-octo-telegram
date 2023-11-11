package com.dnws.wakandaspaceagencyservice.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyTest {

    @ParameterizedTest
    @CsvSource(value = {
            ", 3",
            "MINUTES, ",
            ", ",
            "SECONDS, -1",
    })
    void should_throwException_when_UnitOrValueIsNull(
            TimeUnit unit,
            Long value
    ){
        assertThrows(IllegalArgumentException.class, () ->{
            new Frequency(unit,value);
        });
    }
}