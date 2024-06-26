package com.tr4.db.emailservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.tr4.db.emailservice.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class InboundMessageHandlerImplTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmailFormatter emailFormatter;

    @Mock
    private SendGridEmailService sendGridEmailService;

    @Mock
    private Message<byte[]> message;

    @Mock
    private MessageHeaders messageHeaders;

    @Mock
    private BasicAcknowledgeablePubsubMessage originalMessage;

    @InjectMocks
    private InboundMessageHandlerImpl inboundMessageHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleMessage_Success() throws Exception {
        String payloadJson = "{\n" +
                "    \"eventId\":\"550e8400-e29b-41d4-a716-446655440000\",\n" +
                "    \"eventName\": \"T20 Cricket Finals\",\n" +
                "    \"numberOfTickets\":\"10\",\n" +
                "    \"email\":\"tr4dbgroup@gmail.com\"\n" +
                "}";
        var payLoadBytes = payloadJson.getBytes();

        var toEmail = "tr4dbgroup@gmail.com";
        var ticketCount = 10;
        var eventName = "T20 Cricket Finals";
        var sampleBody = "Sample Body";
        var sampleSubject = "Sample Subject";
        Booking booking = new Booking(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                eventName, ticketCount, toEmail);

        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class)).thenReturn(originalMessage);
        when(message.getPayload()).thenReturn(payLoadBytes);
        when(objectMapper.readValue(payloadJson, Booking.class)).thenReturn(booking);
        when(emailFormatter.generateBody(booking)).thenReturn(sampleBody);
        when(emailFormatter.generateSubject(booking)).thenReturn(sampleSubject);

        //act
        inboundMessageHandler.handleMessage(message);
        verify(emailFormatter, times(1)).generateSubject(booking);
        verify(emailFormatter, times(1)).generateBody(booking);
        verify(sendGridEmailService, times(1)).sendEmail(toEmail, sampleSubject, sampleBody);
        verify(originalMessage, times(1)).ack();
    }

    @Test
    public void testHandleMessage_Failure() throws Exception {
        String payload = "{\"eventName\":\"Concert\",\"numberOfTickets\":2,\"email\":\"user@example.com\"}";
        byte[] payloadBytes = payload.getBytes();

        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class)).thenReturn(originalMessage);
        when(message.getPayload()).thenReturn(payloadBytes);
        when(objectMapper.readValue(payloadBytes, Booking.class)).thenThrow(new IOException("JSON parse error"));

        inboundMessageHandler.handleMessage(message);


        verify(emailFormatter, times(0)).generateSubject(any());
        verify(emailFormatter, times(0)).generateBody(any());
        verify(sendGridEmailService, times(0)).sendEmail(anyString(), anyString(), anyString());
        verify(originalMessage, times(0)).ack();
        verify(originalMessage, times(1)).nack();
    }

}
