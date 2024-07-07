package com.Mail_Bridge_Archive.controller;

import com.Mail_Bridge_Archive.dto.ManualProcessRequest;
import com.Mail_Bridge_Archive.dto.ProcessMailRequest;
import com.Mail_Bridge_Archive.model.MailArchive;
import com.Mail_Bridge_Archive.repository.MailArchiveRepository;
import com.Mail_Bridge_Archive.service.EmailService;
import com.Mail_Bridge_Archive.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private MailArchiveRepository mailArchiveRepository;

    @Autowired
    private EmailService emailService;

    @Value("${mail.service.auth.key}")
    private String validAuthKey;

    @PostMapping("/process")
    public ResponseEntity<String> processMail(@RequestBody ProcessMailRequest request) {
        try {
            mailService.checkMail(request.getSubject());
            return ResponseEntity.ok("{\"message\": \"Mail processed successfully.\"}");
        } catch (Exception e) {
            logger.error("Error processing mail", e);
            emailService.sendExceptionNotification(e.getMessage(), request.getSubject());
            return ResponseEntity.status(500).body("{\"error\": \"Error processing mail: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/manual-insert")
    public ResponseEntity<String> manualInsert(@RequestHeader(value = "Auth-Key") String authKey,
                                               @RequestBody ManualProcessRequest request) {
        if (!validAuthKey.equals(authKey)) {
            logger.warn("Invalid auth key: {}", authKey);
            emailService.sendUnauthorizedNotification(authKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid auth key\"}");
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            MailArchive mailArchive = new MailArchive();
            mailArchive.setName(request.getName());
            mailArchive.setEmail(request.getEmail());
            mailArchive.setMobile(request.getMobile());
            mailArchive.setLocation(request.getLocation());
            mailArchive.setDescription(request.getDescription());
            mailArchive.setStatus(request.getStatus());
            mailArchive.setStartDate(formatter.parse(request.getStartDate()));
            mailArchive.setEndDate(formatter.parse(request.getEndDate()));
            mailArchive.setAssignedTo(request.getAssignedTo());
            mailArchive.setComments(request.getComments());
            mailArchive.setLastUpdated(formatter.parse(request.getLastUpdated()));
            mailArchive.setType(request.getType());
            mailArchive.setHold(request.getHold());

            logger.info("Manual insert data: {}", mailArchive);

            mailArchiveRepository.save(mailArchive);

            logger.info("Data inserted successfully.");
            return ResponseEntity.ok("{\"message\": \"Data inserted successfully.\"}");
        } catch (Exception e) {
            logger.error("Error inserting data", e);
            emailService.sendExceptionNotification(e.getMessage(), request.toString());
            return ResponseEntity.status(500).body("{\"error\": \"Error inserting data: " + e.getMessage() + "\"}");
        }
    }

    public void setValidAuthKey(String validAuthKey) {
        this.validAuthKey = validAuthKey;
    }
}
