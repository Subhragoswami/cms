package com.continuum.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "charging_station_details")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChargingStation {

    @EmbeddedId
    private CompositeId compositeId;
    @Column(name = "location_id", unique = true, nullable = false)
    private long locationId;
    private String name;
    private String parkingType;
    private String email;
    private String phone;
    private String image;
    private double latitude;
    private double longitude;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String status;
    private String hash;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
}
