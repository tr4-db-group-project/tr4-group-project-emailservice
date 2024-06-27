package com.tr4.db.emailservice.controller;

import com.tr4.db.emailservice.EmailserviceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class EmailServiceController {
    private final Logger logger = LoggerFactory.getLogger(EmailserviceApplication.class);
    @GetMapping
    public String getCheckHealthOfService() {
        logger.info("All is well");
        return("Hello from Email Service");
    }

}
