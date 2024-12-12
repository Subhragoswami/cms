package com.continuum.cms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Data
@Configuration
public class CMSServiceConfig {

    @Value("${admin.user.username}")
    private String userName;

    @Value("${admin.user.password}")
    private String password;

    @Value("${admin.user.userRole}")
    private String userRole;

    @Value("${app.sec.key}")
    private String appSecurityKey;

    @Value("${max.login.attempts}")
    private int maxLoginAttempt;

    @Value("${token.expiry.time.hr}")
    private int tokenExpiryTime;

    @Value("${reset.password.token.expiry.time.hr}")
    private int resetTokenExpiryTime;

    @Value("${admin.user.email}")
    private String emailFrom;

    @Value("${cms.frontend.url}")
    private String csmAppURL;

    @Value("${file.upload.maxSize}")
    private Long maxFileUploadSize;

    @Value("${file.upload.extensions}")
    private List<String> allowedFileExt;

    @Value("${password.expiry.period.millis}")
    private long passwordExpiryTime;

    @Value("${scheduled.cron.time}")
    private String cronTime;

    @Value("${resetPassword.retry.duration}")
    private long resetPasswordRetryEmailDuration ;

    @Value("${cms.help.email}")
    private String cmsHelpEmail;

    @Value("${thread.pool.size}")
    private int numberOfThreads;

    @Value("${thread.pool.max.size}")
    private int maxNumberOfThreads;

    @Value("${max.queue.capacity}")
    private int maxQueueSize;

    @Value("${thread.name.prefix}")
    private String threadNamePrefix;

}
