package com.Mail_Bridge_Archive;

import com.Mail_Bridge_Archive.controller.MailController;
import com.Mail_Bridge_Archive.dto.ManualProcessRequest;
import com.Mail_Bridge_Archive.dto.ProcessMailRequest;
import com.Mail_Bridge_Archive.model.MailArchive;
import com.Mail_Bridge_Archive.repository.MailArchiveRepository;
import com.Mail_Bridge_Archive.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MailControllerTest {

    @InjectMocks
    private MailController mailController;

    @Mock
    private MailService mailService;

    @Mock
    private MailArchiveRepository mailArchiveRepository;

    private String validAuthKey = "valid-key"; // Manually setting the validAuthKey

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mailController.setValidAuthKey(validAuthKey); // Set the validAuthKey in the controller
    }

    @Test
    void testProcessMail() throws Exception {
        ProcessMailRequest request = new ProcessMailRequest();
        request.setSubject("Test Subject");
        doNothing().when(mailService).checkMail(request.getSubject());

        ResponseEntity<String> response = mailController.processMail(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Mail processed successfully.\"}", response.getBody());
    }

    @Test
    void testManualInsert_InvalidAuthKey() {
        ManualProcessRequest request = new ManualProcessRequest();
        ResponseEntity<String> response = mailController.manualInsert("invalid-key", request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"Invalid auth key\"}", response.getBody());
    }

    @Test
    void testManualInsert_ValidAuthKey() throws Exception {
        ManualProcessRequest request = new ManualProcessRequest();
        request.setName("Test Name");
        request.setEmail("test@example.com");
        request.setMobile("1234567890");
        request.setLocation("Test Location");
        request.setDescription("Test Description");
        request.setStatus("Active");
        request.setStartDate("2023-07-06 10:00:00");
        request.setEndDate("2023-07-06 18:00:00");
        request.setAssignedTo("Test Assignee");
        request.setComments("Test Comments");
        request.setLastUpdated("2023-07-06 09:00:00");
        request.setType("Test Type");
        request.setHold("No");

        // Create a mock MailArchive object
        MailArchive mailArchive = new MailArchive();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

        // Mock the save method to return the mock MailArchive object
        when(mailArchiveRepository.save(any(MailArchive.class))).thenReturn(mailArchive);

        ResponseEntity<String> response = mailController.manualInsert(validAuthKey, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Data inserted successfully.\"}", response.getBody());
    }
}
