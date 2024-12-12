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
@Table(name = "vendor_account_details")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VendorBankDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    private String cin;
    private String gst;
    private String pan;
    private String tin;
    private String bankAccountName;
    private String bankAccountType;
    private String bankAccountNumber;
    private String ifscCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private Vendor vendor;

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

    public static VendorBankDetails fromVendorRequest(VendorRequest vendorRequest) {
        return VendorBankDetails.builder()
                .cin(vendorRequest.getBankDetailsRequest().getCin())
                .gst(vendorRequest.getBankDetailsRequest().getGst())
                .pan(vendorRequest.getBankDetailsRequest().getPan())
                .tin(vendorRequest.getBankDetailsRequest().getTin())
                .bankAccountName(vendorRequest.getBankDetailsRequest().getBankAccountName())
                .bankAccountType(vendorRequest.getBankDetailsRequest().getBankAccountType())
                .bankAccountNumber(vendorRequest.getBankDetailsRequest().getBankAccountNumber())
                .ifscCode(vendorRequest.getBankDetailsRequest().getIfscCode())
                .build();
    }
}
