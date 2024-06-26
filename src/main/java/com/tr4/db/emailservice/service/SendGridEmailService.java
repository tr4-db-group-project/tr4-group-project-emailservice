package com.tr4.db.emailservice.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

public interface SendGridEmailService {
    void sendEmail(String to, String subject, String body) throws IOException;
}
