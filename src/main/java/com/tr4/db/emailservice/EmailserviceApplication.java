package com.tr4.db.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.tr4.db.emailservice.domain.Booking;
import com.tr4.db.emailservice.service.SendGridEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;


@SpringBootApplication
public class EmailserviceApplication {
    private final Logger logger = LoggerFactory.getLogger(EmailserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EmailserviceApplication.class, args);
    }

    @Value("${booking.event.subscription}")
    private String subscription;

    @Autowired
    private SendGridEmailService sendGridEmailService;

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, subscription);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {


            logger.info("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            String payload = new String((byte[]) message.getPayload());
            ObjectMapper objectMapper = new ObjectMapper();


            try {
                Booking booking = objectMapper.readValue(payload, Booking.class);
                String toEmail = booking.email();
                String eventName = booking.eventName();
                int numOfTickets = booking.numOfTickets();
                String subject = "Booking for " + eventName ;

                String body = "Thank you for your purchase! " +
                        "We are pleased to inform you that your order for " + eventName + " is currently being processed. Here are the details of your reservation:\n\n" +
                        "Event Name: " + eventName + "\n" +
                        "Number of Tickets: " + numOfTickets + "\n\n" +
                        "We will notify you once your booking is confirmed and your tickets are ready. Please allow us some time to complete the processing.\n\n" +
                        "Best regards,\n" +
                        "TR4 team";
                sendGridEmailService.sendEmail(toEmail, subject, body);
                originalMessage.ack(); // Acknowledge the message only if email is sent successfully
                logger.info("Email sent to: " + toEmail);
            } catch (IOException e) {
                logger.error("Failed to parse JSON message", e);
                originalMessage.nack();
            }
        };
    }
}
