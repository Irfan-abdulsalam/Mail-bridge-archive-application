package com.Mail_Bridge_Archive.util;

import com.Mail_Bridge_Archive.model.MailArchive;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class PDFParserUtil {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<MailArchive> parsePDF(File file) throws IOException {
        List<MailArchive> mailArchives = new ArrayList<>();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Assuming each line in the PDF text contains one record
            String[] lines = text.split("\n");
            for (String line : lines) {
                MailArchive mailArchive = new MailArchive();
                String[] fields = line.split(","); // Assuming fields are comma-separated

                if (fields.length >= 13) {
                    mailArchive.setName(fields[0].trim());
                    mailArchive.setEmail(fields[1].trim());
                    mailArchive.setMobile(fields[2].trim());
                    mailArchive.setLocation(fields[3].trim());
                    mailArchive.setDescription(fields[4].trim());
                    mailArchive.setStatus(fields[5].trim());
                    mailArchive.setStartDate(parseDate(fields[6].trim()));
                    mailArchive.setEndDate(parseDate(fields[7].trim()));
                    mailArchive.setAssignedTo(fields[8].trim());
                    mailArchive.setComments(fields[9].trim());
                    mailArchive.setLastUpdated(parseDate(fields[10].trim()));
                    mailArchive.setType(fields[11].trim());
                    mailArchive.setHold(fields[12].trim());

                    mailArchives.add(mailArchive);
                }
            }
        }
        return mailArchives;
    }

    private static Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
