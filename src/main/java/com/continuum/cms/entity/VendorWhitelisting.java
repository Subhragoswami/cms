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

@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "vendor_whitelisting")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VendorWhitelisting {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private Vendor vendor;
    private String fontFamily;
    private String primaryColor;
    private String secondaryColor;
    private String domain;
    private String databaseOnPremise;
    private String vendorEndpoint;
    private UUID fontFile;


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
    public static VendorWhitelisting fromVendorRequest(VendorRequest vendorRequest) {
        return VendorWhitelisting.builder()
                .fontFamily(vendorRequest.getWhitelistingDetails().getFontFamily())
                .primaryColor(vendorRequest.getWhitelistingDetails().getPrimaryColor())
                .secondaryColor(vendorRequest.getWhitelistingDetails().getSecondaryColor())
                .domain(vendorRequest.getWhitelistingDetails().getDomain())
                .databaseOnPremise(vendorRequest.getWhitelistingDetails().getDatabaseOnPremise())
                .vendorEndpoint(vendorRequest.getWhitelistingDetails().getVendorEndpoint())
                .build();
    }
}
