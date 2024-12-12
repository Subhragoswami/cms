package com.continuum.cms.model.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Coordinates {
    private double latitude;
    private double longitude;
}
