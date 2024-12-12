package com.continuum.cms.util;

public final class ErrorConstants {

    //Generic Errors
    public static String MANDATORY_ERROR_CODE = "701";
    public static String MANDATORY_ERROR_MESSAGE = "{0} is mandatory.";

    public static String INVALID_ERROR_CODE = "702";
    public static String INVALID_ERROR_CODE_MESSAGE = "{0} is Invalid.";

    public static String FIELD_NOT_MATCHING_ERROR_CODE = "703";
    public static String FIELD_NOT_MATCHING_ERROR_CODE_MESSAGE = "{0} and {1} are not matching.";

    public static String FIELD_ALREADY_EXIST_ERROR_CODE = "704";
    public static String FIELD_ALREADY_EXIST_ERROR_CODE_MESSAGE = "{0} already exist.";

    public static String DUPLICATE_REQUEST_ERROR_CODE = "705";
    public static String DUPLICATE_REQUEST_ERROR_MESSAGE = "Duplicate Request";

    public static String CANNOT_UPDATE_FIELD_CODE = "706";
    public static String CANNOT_UPDATE_FIELD_MESSAGE = "{0} cannot be updated";

    public static String PATTERN_NOT_MATCH_ERROR_CODE = "707";
    public static String NOT_MATCH_ERROR_MESSAGE_PATTERN = "{0} pattern not matching";

    public static String EXPIRED_ERROR_CODE = "708";
    public static String EXPIRED_ERROR_MESSAGE = "{0} is expired.";
    public static String REPORT_ERROR_CODE = "709";
    public static String REPORT_ERROR_MESSAGE = "Error While Generating Report.";
    public static String PARSE_ERROR_CODE ="710";
    public static String PARSE_ERROR_MESSAGE = "Error While Parsing.";

    public static String INVALID_DATE_RANGE_CODE = "711";
    public static String INVALID_DATE_RANGE_MESSAGE = "End Date Should Be Greater Than Start Date.";

    //User Specific Errors
    public static String NOT_FOUND_ERROR_CODE = "751";
    public static String NOT_FOUND_ERROR_MESSAGE = "{0} Not Found";

    public static String USER_BLOCKED_ERROR_CODE = "752";
    public static String USER_BLOCKED_ERROR_MESSAGE = "User is blocked";

    public static String USER_IN_ACTIVE_ERROR_CODE = "753";
    public static String USER_IN_ACTIVE_ERROR_MESSAGE = "User is in-active";

    public static String USER_DUPLICATE_SESSION_ERROR_CODE = "754";
    public static String USER_DUPLICATE_SESSION_ERROR_MESSAGE = "Duplicate user session";

    public static String USER_PASSWORD_EXPIRED_ERROR_CODE = "755";
    public static String USER_PASSWORD_EXPIRED_ERROR_MESSAGE = "Duplicate user session";

    public static String OLD_AND_NEW_PASSWORD_SAME_ERROR_CODE = "756";
    public static String OLD_AND_NEW_PASSWORD_SAME_ERROR_CODE_MESSAGE = "Old password and New password cannot be same";
    public static  String NEW_AND_CONFIRM_PASSWORD_NOT_SAME_ERROR_CODE = "757";
    public static  String NEW_AND_CONFIRM_PASSWORD_NOT_SAME_ERROR_CODE_MASSAGE = "New password and Confirm password must be same";

    //Vendor Specific Errors
    public static String VENDOR_INACTIVE_ERROR_CODE = "771";
    public static String VENDOR_INACTIVE_ERROR_MESSAGE = "Vendor is Inactive";

    public static String VENDOR_BLOCKED_ERROR_CODE = "772";
    public static String VENDOR_BLOCKED_ERROR_MESSAGE = "Vendor is Inactive";

    public static String MAX_LIMIT_REACHED_VENDOR_USER_CREATION_ERROR_CODE = "773";
    public static String MAX_LIMIT_REACHED_VENDOR_USER_CREATION_MESSAGE = "Vendor Admin Creation limit exceeded";


    //Other Specific Error
    public static String ENCRYPTION_ERROR_CODE = "600";
    public static String ENCRYPTION_ERROR_MESSAGE = "Error in encryption";

    public static String DECRYPTION_ERROR_CODE = "601";
    public static String DECRYPTION_ERROR_MESSAGE = "Error in decryption";


    //
    public static String CLIENT_ERROR_CODE = "650";
    public static String CLIENT_ERROR_MESSAGE = "Client error";

    public static String SYSTEM_ERROR_CODE = "500";

}
