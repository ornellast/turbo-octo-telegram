package com.dnws.wakandaspaceagencyservice.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract sealed class BaseEntity permits SatelliteEntity {


    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @CreatedDate
    Instant createdAt = null;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @LastModifiedDate
    Instant updatedAt = null;

    @Version
    private Integer version;
}
