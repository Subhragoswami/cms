package com.continuum.cms.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MasterDataType {

    STATUS("STATUS"),ACCOUNT("ACCOUNT"),DATAPROVIDER("DATAPROVIDER"),DATABASE("DATABASE");

    private final String name;
}
