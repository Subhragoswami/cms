package com.continuum.cms.entity;

import com.continuum.cms.model.request.VendorRequest;
import com.continuum.cms.util.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "vendor")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Vendor {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;
    private String name;
    private String status;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String pincode;


    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    @LastModifiedBy
    private String updatedBy;

    @CreatedBy
    private String createdBy;

    public static Vendor fromVendorRequest(VendorRequest vendorRequest) {
        return Vendor.builder()
                .name(vendorRequest.getName())
                .status(Status.ACTIVE.getName())
                .address1(vendorRequest.getAddress().getAddress1())
                .address2(vendorRequest.getAddress().getAddress2())
                .city(vendorRequest.getAddress().getCity())
                .state(vendorRequest.getAddress().getState())
                .country(vendorRequest.getAddress().getCountry())
                .pincode(vendorRequest.getAddress().getPincode())
                .build();
    }

}
