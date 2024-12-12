package com.continuum.cms.model.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankDetailsRequest {

    private String cin;
    private String gst;
    private String pan;
    private String tin;
    private String bankAccountName;
    private String bankAccountType;
    private String bankAccountNumber;
    private String ifscCode;
}
