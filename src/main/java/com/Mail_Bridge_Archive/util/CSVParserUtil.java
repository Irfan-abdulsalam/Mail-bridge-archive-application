package com.Mail_Bridge_Archive.util;

import com.Mail_Bridge_Archive.model.MailArchive;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVParserUtil {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static List<MailArchive> parseCSV(File file) throws IOException {
        List<MailArchive> mailArchives = new ArrayList<>();
        try (FileReader reader = new FileReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                MailArchive mailArchive = new MailArchive();
                mailArchive.setName(csvRecord.get("name"));
                mailArchive.setEmail(csvRecord.get("email"));
                mailArchive.setMobile(csvRecord.get("mobile"));
                mailArchive.setLocation(csvRecord.get("location"));
                mailArchive.setDescription(csvRecord.get("description"));
                mailArchive.setStatus(csvRecord.get("status"));
                mailArchive.setStartDate(parseDate(csvRecord.get("start_date")));
                mailArchive.setEndDate(parseDate(csvRecord.get("end_date")));
                mailArchive.setAssignedTo(csvRecord.get("assigned_to"));
                mailArchive.setComments(csvRecord.get("comments"));
                mailArchive.setLastUpdated(parseDate(csvRecord.get("last_updated")));
                mailArchive.setType(csvRecord.get("type"));
                mailArchive.setHold(csvRecord.get("hold"));

                mailArchives.add(mailArchive);
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
