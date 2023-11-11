package com.dnws.wakandaspaceagencyservice.persistence;


import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.ReadingFrequency;
import com.dnws.wakandaspaceagencyservice.model.ScannedZone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "space_agency_service", name = "satellite")
public final class SatelliteEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private ReadingFrequency readingFrequency;

    @Column(nullable = false)
    private boolean isActive = true;

    @Enumerated
    @Column(nullable = false)
    private SatelliteType type;


    @Column(nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ScannedZone> scannedZones;

    public List<ScannedZone> getScannedZones() {
        return new ArrayList<>(scannedZones);
    }
}
