package com.continuum.cms.util;


public final class AppConstants {

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_FAILURE = 1;
    public static final String PASSWORD_CHANGE_SUCCESS_MESSAGE = "Password changed successfully.";
    public static final String PASSWORD_RESET_SUCCESS_MESSAGE = "Password Reset successfully.";
    public static final String PASSWORD_RESET_INITIATION_SUCCESS_MESSAGE = "Password Request Initiation Completed.";

    public static final String REPORT_GENERATION_SUCCESS_MESSAGE = "Report Generated Successfully..!";


    public static final String DETAILS_CAPTURED_SUCCESSFULLY = "Details captured successfully";
    public static final String CSV_GENERATED_SUCCESSFULLY = "csv generated successfully";
    public static final String RESET_PASSWORD_INITIATE_SUBJECT = "Reset Password Link";
    public static String RESET_PASSWORD_UPDATED_SUBJECT = "Password Reset Confirmation";
    public static String CHANGE_PASSWORD_CONFIRMATION_SUBJECT = "Password Change Confirmation";
    public static String PASSWORD_CHANGE_REQUEST_SUBJECT = "Password Change Request";
    public static final String USER_ONBOARDING_SUBJECT = "Vendor Admin Onboarding";
    public static String HELP_SCREEN_SUBJECT = "Assistance Request";
    public static final String CARBON_CREDIT = "carbon_credit";
    public static final String VENDOR = "vendor";

    public static final String CATEGORY_VENDOR = "Vendor";

    public static final String CATEGORY_CARBON_CREDIT = "Carbon Credit";

    public static final String VENDOR_FILE_NAME = "VendorReport";

    public static final String CARBON_CREDIT_FILE_NAME = "CarbonCreditReport";

    public static final String HEADER_FOR_VENDOR = "Name,Status,Address,City,State,Pin Code,Country,Bank Account Name,Bank Account Type,CIN,IFSC Code," +
            "PAN,GST,TIN,Bank Account Number, Onboarded Date,Total Admins Onboarded";

    public static final String HEADER_FOR_CARBON_CREDIT = "Vendor Name, Vendor ID, Charging station name, Start Date time, End Date time," +
            " Energy Utilized (kwh), User name, email, Mobile, Vehicle type, Location, Latitude, Longitude, Session ID, Onboarded Date, Carbon Credits";


    public static String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[\\W])(?=.*[0-9])(?=.*[a-z]).{8,15}$";

    public static String PAN_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    public static String GST_PATTERN = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";

    public static String baseUrl = "https://cms-api.dev.electrolitemobility.com/continuum";

    public static String REDIS_DATA_KEY = "ADMIN_DASHBOARD";

}

