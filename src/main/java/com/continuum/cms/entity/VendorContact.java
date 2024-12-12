package com.continuum.cms.entity;

import com.continuum.cms.model.request.VendorRequest;
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
@Table(name = "vendor_contact")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VendorContact {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private Vendor vendor;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

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

    public static VendorContact fromVendorRequest(VendorRequest vendorRequest) {
        return VendorContact.builder()
                .firstName(vendorRequest.getContactDetails().getFirstName())
                .lastName(vendorRequest.getContactDetails().getLastName())
                .email(vendorRequest.getContactDetails().getEmail())
                .phoneNumber(vendorRequest.getContactDetails().getPhoneNumber())
                .build();
    }
}
