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

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vendor_customer")
public class VendorCustomer {
    @EmbeddedId
    private CompositeId compositeId;
    private String mobile;
    private String email;
    private String firstName;
    private String lastName;
    private String userType;
    @Column(name = "transaction_id")
    private String transactionId;

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
