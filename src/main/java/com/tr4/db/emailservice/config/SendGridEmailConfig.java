package com.tr4.db.emailservice.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridEmailConfig {
    private final String sendgridApiKey;

    public SendGridEmailConfig(@Value("${sendgrid.api-key}") String sendgridApiKey) {
        this.sendgridApiKey = sendgridApiKey;
    }

    @Bean
    public SendGrid getSendGrid() {
        return new SendGrid(this.sendgridApiKey);
    }
}
