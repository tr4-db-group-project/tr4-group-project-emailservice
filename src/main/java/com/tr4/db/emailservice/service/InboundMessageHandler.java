package com.tr4.db.emailservice.service;

import org.springframework.messaging.Message;


public interface InboundMessageHandler {
    void handleMessage(Message<?> message);
}
