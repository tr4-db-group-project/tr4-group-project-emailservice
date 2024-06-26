package com.tr4.db.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.tr4.db.emailservice.model.Booking;
import com.tr4.db.emailservice.service.InboundMessageHandler;
import com.tr4.db.emailservice.service.SendGridEmailService;
import com.tr4.db.emailservice.service.SendGridEmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
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
    private InboundMessageHandler inboundMessageHandler;

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
            inboundMessageHandler.handleMessage(message);
        };
    }
}
