package com.dnws.wakandaspaceagencyservice.reader;

import com.dnws.wakandaspaceagencyservice.model.Zone;

import java.util.List;

public interface ISatelliteDataReader<T> {
    T read();
}
