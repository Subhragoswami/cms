package com.continuum.cms.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageType {
    LOGO("logo"),
    FONT("font"),
    PROFILEPIC("profilePic");

    private final String name;
}
