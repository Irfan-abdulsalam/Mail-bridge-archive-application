package com.Mail_Bridge_Archive.service;

import com.Mail_Bridge_Archive.model.MailArchive;
import com.Mail_Bridge_Archive.repository.MailArchiveRepository;
import com.Mail_Bridge_Archive.util.CSVParserUtil;
import com.Mail_Bridge_Archive.util.ExcelParserUtil;
import com.Mail_Bridge_Archive.util.PDFParserUtil;
import com.Mail_Bridge_Archive.util.TXTParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailArchiveRepository mailArchiveRepository;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Scheduled(fixedRate = 60000) // 600000 milliseconds = 10 minutes
    public void checkMail() {
        checkMail("Mail Archive");
    }

    public void checkMail(String subjectKeyword) {
        logger.info("Scheduled task to check mail started.");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session emailSession = Session.getDefaultInstance(properties);

        try (Store store = emailSession.getStore()) {
            store.connect("imap.gmail.com", mailUsername, mailPassword);

            try (Folder emailFolder = store.getFolder("INBOX")) {
                emailFolder.open(Folder.READ_WRITE);
                Message[] messages = emailFolder.getMessages();

                for (Message message : messages) {
                    if (!message.isSet(Flags.Flag.SEEN) && message.getSubject().contains(subjectKeyword)) {
                        logger.info("Processing unseen email: {}", message.getSubject());
                        boolean success = processMessage(message);
                        moveMessage((MimeMessage) message, store, success);
                        message.setFlag(Flags.Flag.SEEN, true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while checking mail", e);
        }

        logger.info("Scheduled task to check mail completed.");
    }

    private boolean processMessage(Message message) {
        try {
            if (message.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("multipart/*")) {
                        processMultipart((Multipart) bodyPart.getContent());
                    } else if (isAttachment(bodyPart)) {
                        saveAttachment(bodyPart);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error processing message", e);
            return false;
        }
    }

    private void processMultipart(Multipart multipart) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (isAttachment(bodyPart)) {
                saveAttachment(bodyPart);
            }
        }
    }

    private boolean isAttachment(BodyPart bodyPart) throws Exception {
        return bodyPart.getDisposition() != null &&
                (bodyPart.isMimeType("application/pdf") ||
                        bodyPart.isMimeType("application/vnd.ms-excel") ||
                        bodyPart.isMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                        bodyPart.isMimeType("text/csv"));
    }

    private void saveAttachment(BodyPart bodyPart) throws Exception {
        String fileName = bodyPart.getFileName();
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        logger.info("Saving attachment: {}", fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             InputStream is = bodyPart.getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        // Log the received data before parsing
        logger.info("Received data from attachment: {}", fileName);

        List<MailArchive> mailArchives;
        if (fileName.endsWith(".csv")) {
            mailArchives = CSVParserUtil.parseCSV(file);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            mailArchives = ExcelParserUtil.parseExcel(file);
        } else if (fileName.endsWith(".pdf")) {
            mailArchives = PDFParserUtil.parsePDF(file);
        } else if (fileName.endsWith(".txt")) {
            mailArchives = TXTParserUtil.parseTXT(file);
        } else {
            logger.warn("Unsupported file type: {}", fileName);
            return; // Unsupported file type
        }

        // Log the parsed data before saving to the database
        for (MailArchive archive : mailArchives) {
            logger.info("Parsed data from attachment: {}", archive);
        }

        mailArchiveRepository.saveAll(mailArchives);
        logger.info("Saved mail archive records for file: {}", fileName);
    }


    private void moveMessage(MimeMessage message, Store store, boolean success) {
        String targetFolderName = success ? "Processed_Archive" : "Failed_Archive";
        try {
            Folder targetFolder = store.getFolder(targetFolderName);
            if (!targetFolder.exists()) {
                targetFolder.create(Folder.HOLDS_MESSAGES);
            }
            targetFolder.open(Folder.READ_WRITE);

            Folder sourceFolder = message.getFolder();
            sourceFolder.copyMessages(new Message[]{message}, targetFolder);
            message.setFlag(Flags.Flag.DELETED, true);

            targetFolder.close(false);
            logger.info("Moved email to folder: {}", targetFolderName);
        } catch (MessagingException e) {
            logger.error("Error moving email to folder: {}", targetFolderName, e);
        }
    }
}
