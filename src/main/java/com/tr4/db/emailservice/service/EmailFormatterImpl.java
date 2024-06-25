package com.tr4.db.emailservice.service;

import com.tr4.db.emailservice.model.Booking;
import org.springframework.stereotype.Service;

@Service
public class EmailFormatterImpl implements EmailFormatter {
    @Override
    public String generateSubject(Booking booking) {
        return String.format("Booking for %s" , booking.eventName());
    }

    @Override
    public String generateBody(Booking booking) {
            var body = new StringBuilder();
            body.append("Thank you for your purchase! \n");
            body.append(String.format("We are pleased to inform you that your order for %s ", booking.eventName()));
            body.append("is currently being processed. Here are the details of your reservation:\n\n");
            body.append(String.format("Event Name: %s \n", booking.eventName()));
            body.append(String.format("Number of Tickets: %d \n\n", booking.numOfTickets()));
            body.append("We will notify you once your booking is confirmed and your tickets are ready. Please allow us some time to complete the processing.\n\n");
            body.append("Best regards,\n");
            body.append("TR4 team");
            return body.toString();
    }
}
