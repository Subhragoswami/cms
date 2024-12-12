package com.continuum.cms.util.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HashKey {

    CARBON_CREDITS_HASHKEY("CARBON_CREDITS_HASHKEY", "CC"),
    CHARGING_SESSION_HASHKEY("CHARGING_SESSION_HASHKEY","CSS"),
    CHARGING_STATION_HASHKEY("CHARGING_STATION_HASHKEY","CS"),
    VENDOR_CUSTOMER_HASHKEY("VENDOR_CUSTOMER_HASHKEY","VS"),
    SUPER_ADMIN_DASHBOARD_DATA("SUPER_ADMIN_DASHBOARD_DATA", "CSAD");

    public final String name;
    public final String value;
}
