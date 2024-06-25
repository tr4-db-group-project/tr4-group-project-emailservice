package com.tr4.db.emailservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.tr4.db.emailservice.EmailserviceApplication;
import com.tr4.db.emailservice.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class InboundMessageHandlerImpl implements InboundMessageHandler {
    private final Logger logger = LoggerFactory.getLogger(EmailserviceApplication.class);

    private final ObjectMapper objectMapper;
    private final EmailFormatter emailFormatter;
    private final SendGridEmailService sendGridEmailService;

    @Autowired
    public InboundMessageHandlerImpl(ObjectMapper objectMapper, EmailFormatter emailFormatter, SendGridEmailService sendGridEmailService) {
        this.objectMapper = objectMapper;
        this.emailFormatter = emailFormatter;
        this.sendGridEmailService = sendGridEmailService;
    }

    @Override
    public void handleMessage(Message<?> message) {
        BasicAcknowledgeablePubsubMessage originalMessage =
                message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);

        String payload = new String((byte[]) message.getPayload());
        try {
            Booking booking = objectMapper.readValue(payload, Booking.class);
            sendFormattedEmail(booking);
            acknowledgeMessage(originalMessage);
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            originalMessage.nack();
        }
    }

    private void sendFormattedEmail(Booking booking) throws Exception {
        String toEmail = booking.email();
        String subject = emailFormatter.generateSubject(booking);
        String emailBody = emailFormatter.generateBody(booking);
        sendGridEmailService.sendEmail(toEmail, subject, emailBody);
        logger.info("Email sent to: {}", toEmail);
    }

    private void acknowledgeMessage(BasicAcknowledgeablePubsubMessage originalMessage) {
        originalMessage.ack();
        logger.info("Message acknowledged successfully");
    }
}
