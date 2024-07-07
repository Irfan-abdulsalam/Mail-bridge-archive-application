package com.Mail_Bridge_Archive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailBridgeArchiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailBridgeArchiveApplication.class, args);
    }
}
