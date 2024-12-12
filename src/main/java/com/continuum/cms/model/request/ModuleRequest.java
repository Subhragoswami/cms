package com.continuum.cms.model.request;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ModuleRequest {

    private UUID id;
    private boolean active;
}
