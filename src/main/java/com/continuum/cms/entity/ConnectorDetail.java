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
@Table(name = "connector_details")
@EntityListeners(AuditingEntityListener.class)
public class ConnectorDetail {

    @EmbeddedId
    private CompositeId compositeId;
    private String name;
    private String standardName;
    private String formatName;
    private String powerType;
    private String cmsId;
    private int maxVoltage;
    private int maxAmperage;
    private int maxElectricPower;
    private String termsConditionUrl;
    private String connectorImage;
    private String evseId;
    private String connectorId;

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
