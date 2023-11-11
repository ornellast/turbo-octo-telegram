package com.dnws.wakandaspaceagencyservice.reader.impl;

import com.dnws.wakandaspaceagencyservice.enums.WindRose;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.reader.ISatelliteDataReader;

import java.util.List;
import java.util.Random;

public class InfraredReader implements ISatelliteDataReader<String> {
    private List<Zone> zones;

    public InfraredReader(List<Zone> zones){
        this.zones = zones;
    }

    @Override
    public String read() {
        return "This is an infrared reading";
    }
}
