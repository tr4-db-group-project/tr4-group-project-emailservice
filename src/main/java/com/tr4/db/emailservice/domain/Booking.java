package com.tr4.db.emailservice.domain;

public record Booking(
        String eventid,
        String eventName,
        int numOfTickets,
        String email
) {}
