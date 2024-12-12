package com.continuum.cms.model.request;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class HelpRequest {

    private UUID userId;
    private String userName;
    private String descriptionOfHelp;
}
