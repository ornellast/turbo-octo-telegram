package com.dnws.wakandaspaceagencyservice.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ReadingFrequencyTest {

    @ParameterizedTest
    @CsvSource(value = {
            ", 3",
            "MINUTES, ",
            ", ",
    })
    void should_throwException_when_UnitOrValueIsNull(
            TimeUnit unit,
            Integer value
    ){
        assertThrows(IllegalArgumentException.class, () ->{
            new ReadingFrequency(unit,value);
        });
    }
}