package com.tr4.db.emailservice.service;

import com.tr4.db.emailservice.model.Booking;

public interface EmailFormatter {
    String generateSubject(Booking booking);
    String generateBody(Booking booking);
}
