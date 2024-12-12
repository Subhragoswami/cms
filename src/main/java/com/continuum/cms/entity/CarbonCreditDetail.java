package com.continuum.cms.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carbon_credit_details")
@EntityListeners(AuditingEntityListener.class)
public class CarbonCreditDetail {

    @EmbeddedId
    private CompositeId compositeId;

    private String status;
    private BigDecimal creditScorePoints;
    private Long stationId;
    @Column(name = "transaction_id")
    private String transactionId;
    private String certificateNumber;

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
