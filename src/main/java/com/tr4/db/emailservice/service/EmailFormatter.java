package com.tr4.db.emailservice.service;

import com.tr4.db.emailservice.model.Booking;
import org.springframework.stereotype.Service;

public interface EmailFormatter {
    String generateSubject(Booking booking);
    String generateBody(Booking booking);
}
