package com.continuum.cms.model.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class VendorModulePostRequest {
    private UUID vendorId;
    private List<UUID> modules;
}
