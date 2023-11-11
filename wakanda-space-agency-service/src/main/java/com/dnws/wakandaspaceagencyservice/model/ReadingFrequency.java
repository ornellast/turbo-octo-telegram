package com.dnws.wakandaspaceagencyservice.model;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public record ReadingFrequency(TimeUnit unit, Integer value) {
}
