package com.Mail_Bridge_Archive;

import com.Mail_Bridge_Archive.model.MailArchive;
import com.Mail_Bridge_Archive.repository.MailArchiveRepository;
import com.Mail_Bridge_Archive.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

import static org.mockito.Mockito.*;

class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private MailArchiveRepository mailArchiveRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCheckMail() throws Exception {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        mailService.checkMail("Test Subject");

        // Add your assertions and verify methods as per your logic
    }
}
