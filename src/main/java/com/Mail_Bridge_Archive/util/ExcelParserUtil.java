package com.Mail_Bridge_Archive.util;

import com.Mail_Bridge_Archive.model.MailArchive;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelParserUtil {

    public static List<MailArchive> parseExcel(File file) throws IOException {
        List<MailArchive> mailArchives = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                MailArchive mailArchive = new MailArchive();
                mailArchive.setName(getCellValue(row.getCell(0)));
                mailArchive.setEmail(getCellValue(row.getCell(1)));
                mailArchive.setMobile(getCellValue(row.getCell(2)));
                mailArchive.setLocation(getCellValue(row.getCell(3)));
                mailArchive.setDescription(getCellValue(row.getCell(4)));
                mailArchive.setStatus(getCellValue(row.getCell(5)));
                mailArchive.setStartDate(getDateCellValue(row.getCell(6)));
                mailArchive.setEndDate(getDateCellValue(row.getCell(7)));
                mailArchive.setAssignedTo(getCellValue(row.getCell(8)));
                mailArchive.setComments(getCellValue(row.getCell(9)));
                mailArchive.setLastUpdated(getDateCellValue(row.getCell(10)));
                mailArchive.setType(getCellValue(row.getCell(11)));
                mailArchive.setHold(getCellValue(row.getCell(12)));

                mailArchives.add(mailArchive);
            }
        }
        return mailArchives;
    }

    private static String getCellValue(Cell cell) {
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : "";
    }

    private static Date getDateCellValue(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC ? cell.getDateCellValue() : null;
    }
}
