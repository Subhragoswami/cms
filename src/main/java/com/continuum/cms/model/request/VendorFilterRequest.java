package com.continuum.cms.model.request;

import lombok.*;

import java.util.List;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorFilterRequest {

    private List<String> status;
    private List<String> modules;
}
