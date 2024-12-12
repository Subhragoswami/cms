package com.continuum.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "charging_sessions")
@EntityListeners(AuditingEntityListener.class)
public class ChargingSession {

    @EmbeddedId
    private CompositeId compositeId;

    private LocalDateTime startAt;
    private LocalDateTime stopAt;

    private String evseId;
    private String identity;
    private Integer connectorNumber;
    private String connectorId;

    @Column(name = "location_id")
    private long locationId;
    private String locationName;
    private double latitude;
    private double longitude;
    private String address;

    @Column(name = "transaction_id")
    private String transactionId;
    private String vehicleName;
    private String vehicleNumber;
    private String vehicleType;
    private double usedEnergy;
    private String currency;
    private String authorizationReference;
    private String cdrToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id", insertable = false, updatable = false)
    private VendorCustomer vendorCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", insertable = false, updatable = false)
    private ChargingStation chargingStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorCode", referencedColumnName = "cin", insertable = false, updatable = false)
    private VendorBankDetails vendorBankDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id", insertable = false, updatable = false)
    private CarbonCreditDetail carbonCreditDetail;

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
