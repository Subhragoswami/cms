package com.continuum.cms.service;

import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.EmailAuditDao;
import com.continuum.cms.entity.EmailAudit;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.DateUtil;
import com.continuum.cms.util.enums.EmailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.stream.Collectors;

import static com.continuum.cms.util.enums.EmailType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailAuditDao emailAuditDao;
    private final CMSServiceConfig cmsServiceConfig;

    public void resetPasswordInitiateEmail(String receiverEmailId, User user, String token) {
        log.info("Request received for send email to emailId: [{}]", receiverEmailId);
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("resetLink", cmsServiceConfig.getCsmAppURL()+"/reset-password?token="+token);
        context.setVariable("expiryTime", DateUtils.addHours(DateUtil.getDate(), cmsServiceConfig.getResetTokenExpiryTime()));
        sendEmail(AppConstants.RESET_PASSWORD_INITIATE_SUBJECT, context, receiverEmailId, RESET_PASSWORD_INITIATE, null);
        emailAuditDao.saveEmailAudit(buildEmailAudit(user, RESET_PASSWORD_INITIATE.getName(), getContextString(context)));
    }
    public void resetPasswordUpdatedEmail(String receiverEmailId, User user) {
        log.info("Request received for send email to emailId: [{}]", receiverEmailId);
        Context context = new Context();
        context.setVariable("user", user);
        sendEmail(AppConstants.RESET_PASSWORD_UPDATED_SUBJECT, context, receiverEmailId, RESET_PASSWORD_CONFIRM, null);
        emailAuditDao.saveEmailAudit(buildEmailAudit(user, RESET_PASSWORD_CONFIRM.getName(), getContextString(context)));
    }

    public void helpScreenEmail(String receiverEmailId, User user, Vendor vendor, String description) {
        log.info("Request received for send email for help Screen to emailId: [{}]", receiverEmailId);
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("vendor", vendor);
        context.setVariable("description", description);
        sendEmail(AppConstants.HELP_SCREEN_SUBJECT, context, receiverEmailId, HELP_SCREEN, user.getUsername());
        emailAuditDao.saveEmailAudit(buildEmailAudit(user, HELP_SCREEN.getName(), getContextString(context)));
    }

    public void changePasswordConfirmationEmail(String receiverEmailId, User user){
        log.info("Request received for send email to emailId: [{}]", receiverEmailId);
        Context context = new Context();
        context.setVariable("user", user);
        sendEmail(AppConstants.CHANGE_PASSWORD_CONFIRMATION_SUBJECT, context, receiverEmailId, CHANGE_PASSWORD_CONFIRM, null);
        emailAuditDao.saveEmailAudit(buildEmailAudit(user, CHANGE_PASSWORD_CONFIRM.getName(), getContextString(context)));
    }

    public void newUserOnboardingEmail(User user, String token) {
        log.info("Request received to send onboarding Vendor Admin Email to emailId: [{}]", user.getEmail());
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("username", user.getEmail());
        context.setVariable("resetLink", cmsServiceConfig.getCsmAppURL()+"/reset-password?token="+token);
        context.setVariable("expiryTime", DateUtils.addHours(DateUtil.getDate(), cmsServiceConfig.getResetTokenExpiryTime()));
        sendEmail(AppConstants.USER_ONBOARDING_SUBJECT, context, user.getEmail(), USER_ONBOARDING, null);
        emailAuditDao.saveEmailAudit(buildEmailAudit(user, USER_ONBOARDING.getName(), getContextString(context)));
    }
     void sendEmail(String subject, Context context, String receiverEmailId, EmailType emailType, String replyTo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(cmsServiceConfig.getEmailFrom()));
            message.setRecipients(MimeMessage.RecipientType.TO, receiverEmailId);
            message.setRecipients(MimeMessage.RecipientType.TO, receiverEmailId);
            message.setSubject(subject);
            message.setContent(getHtmlContent(context, emailType), "text/html; charset=utf-8");  // Set the HTML content as a String

            if (replyTo != null && !replyTo.isEmpty()) {
                message.setReplyTo(new InternetAddress[]{new InternetAddress(replyTo)});
            }

            mailSender.send(message);
            log.info("email sent successfully to : {}", receiverEmailId);
        } catch (MessagingException e) {
            log.error("There is some error sending email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getHtmlContent(Context context, EmailType emailType) {
        return switch (emailType) {
            case RESET_PASSWORD_INITIATE -> getHTMLContent(context, "resetPasswordInitiate");
            case RESET_PASSWORD_CONFIRM -> getHTMLContent(context, "resetPasswordUpdated");
            case CHANGE_PASSWORD_CONFIRM -> getHTMLContent(context, "changePasswordConfirmation");
            case USER_ONBOARDING -> getHTMLContent(context, "userOnBoarding");
            case HELP_SCREEN -> getHTMLContent(context, "helpScreen");
        };
    }

    private String getHTMLContent(Context context, String templateName) {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setSuffix(".html");
        templateEngine.setTemplateResolver(resolver);
        return templateEngine.process(templateName, context);
    }

    private EmailAudit buildEmailAudit(User user, String emailType, String emailPlaceHolder){
        return EmailAudit.builder()
                .userId(user.getId())
                .emailType(emailType)
                .emailPlaceholder(emailPlaceHolder)
                .build();
    }
    private static String getContextString(Context context) {
        return context.getVariableNames().stream().map(variableName -> "{" + variableName + " : " + context.getVariable(variableName) + "},").collect(Collectors.joining());
    }
}
