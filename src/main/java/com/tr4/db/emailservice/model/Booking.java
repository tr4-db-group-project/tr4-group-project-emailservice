package com.tr4.db.emailservice.model;

public record Booking(
        String eventid,
        String eventName,
        int numOfTickets,
        String email
) {}
