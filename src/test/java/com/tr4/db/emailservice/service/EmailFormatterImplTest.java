package com.tr4.db.emailservice.service;

import com.tr4.db.emailservice.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailFormatterImplTest {

    private EmailFormatterImpl emailFormatter;

    @BeforeEach
    public void setUp() {
        emailFormatter = new EmailFormatterImpl();
    }

    @Test
    public void testGenerateSubject() {
        Booking booking = new Booking(UUID.randomUUID(),"Concert", 2, "tr4dbgroup@gmail.com");
        String expectedSubject = "Booking for Concert";
        String actualSubject = emailFormatter.generateSubject(booking);
        assertEquals(expectedSubject, actualSubject);
    }


    @Test
    public void testGenerateBody() {
        Booking booking = new Booking(UUID.randomUUID(),"Concert", 2, "tr4dbgroup@gmail.com");
        String expectedBody = "Thank you for your purchase! \n" +
                "We are pleased to inform you that your order for Concert " +
                "is currently being processed. Here are the details of your reservation:\n\n" +
                "Event Name: Concert \n" +
                "Number of Tickets: 2 \n\n" +
                "We will notify you once your booking is confirmed and your tickets are ready. Please allow us some time to complete the processing.\n\n" +
                "Best regards,\n" +
                "TR4 team";
        String actualBody = emailFormatter.generateBody(booking);
        assertEquals(expectedBody, actualBody);
    }
}
