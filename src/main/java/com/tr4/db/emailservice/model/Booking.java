package com.tr4.db.emailservice.model;

public record Booking(
        java.util.UUID eventId,
        String eventName,
        Integer numberOfTickets,
        String email
) {}
