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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "charger_details")
@EntityListeners(AuditingEntityListener.class)
public class ChargerDetail {

    @EmbeddedId
    private CompositeId compositeId;
    private String identity;
    private String chargerName;
    private String chargePointOem;
    private String chargePointDevice;
    private String chargePointConnectionProtocol;
    private String floorLevel;
    private String qrCode;
    private String chargerId;
    private String stationType;
    private long locationId;
    private String chargerStatus;

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
