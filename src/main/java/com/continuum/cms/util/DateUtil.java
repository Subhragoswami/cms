package com.continuum.cms.util;

import com.continuum.cms.exceptions.CMSException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

@Component
@Slf4j
public class DateUtil {
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Value("${password.expiry.period.millis}")
    private long passwordExpiryPeriodMillis;

    public static Date getDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new Date();
    }

    public static Date conversion(Date lastLogin) {
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
            String formattedDate = outputFormat.format(lastLogin);
            return outputFormat.parse(formattedDate);
        } catch (Exception ex) {
            log.error("There is some error in conversion of date: {}", ex.getMessage());
            throw new CMSException(ErrorConstants.SYSTEM_ERROR_CODE, ex.getMessage());
        }
    }

    public Date getPasswordExpiryTime() {
        return new Date(getDate().getTime() - passwordExpiryPeriodMillis);
    }

    public static String formatDateToString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN).format(date);
    }

    public static String getDateFormat(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static String getISTToUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(IST_ZONE).withZoneSameInstant(ZoneOffset.UTC).toString();
    }
}
