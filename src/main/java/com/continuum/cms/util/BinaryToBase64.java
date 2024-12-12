package com.continuum.cms.util;

import java.util.Base64;

public class BinaryToBase64 {
    public static String convertToBase64(byte[] binaryData) {
        String base64Encoded = Base64.getEncoder().encodeToString(binaryData);
        return base64Encoded;
    }

}
