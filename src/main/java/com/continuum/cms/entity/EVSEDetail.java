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
@Table(name = "evse_details")
@EntityListeners(AuditingEntityListener.class)
public class EVSEDetail {

    @EmbeddedId
    private CompositeId compositeId;
    private String physicalReference;
    private String chargerId;
    private String maxOutputPower;
    private String status;
    private int connectorId;
    private String connectorStatus;
    private String availability;
    private String evseId;

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
