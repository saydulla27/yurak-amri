package uz.pdp.yurakamri.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uz.pdp.yurakamri.entity.HelpAndUsers;


import java.io.*;
import java.util.List;

public class Excell {




    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet;
    private List<HelpAndUsers> listUsers;

    public Excell(List<HelpAndUsers> listUsers) {
        this.listUsers = listUsers;
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Hisobot");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Ism Familyasi", style);
        createCell(row, 1, "Tugilgan yili", style);
        createCell(row, 2, "Telefon raqami", style);
        createCell(row, 3, "Manzil", style);
        createCell(row, 4, "Turmush örtogi", style);
        createCell(row, 5, "Farzandlar soni", style);
        createCell(row, 6, "Farzandlarini yoshlari", style);
        createCell(row, 7, "Yordam turi", style);
        createCell(row, 8, "Qöshimcha malumot", style);
        createCell(row, 9, "Sana", style);
        createCell(row, 10, "Kim yordam bergani", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {


        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setFontHeight(14);
        style.setFont(font);

        for (HelpAndUsers user : listUsers) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;


            createCell(row, columnCount++, user.getUsers().getFullName(), style);
            createCell(row, columnCount++, user.getUsers().getAge(), style);
            createCell(row, columnCount++, user.getUsers().getPhoneNumber(), style);
            createCell(row, columnCount++, user.getUsers().getRayon()+" "+user.getUsers().getCity()+" "+user.getUsers().getStreet_home(), style);
            createCell(row, columnCount++, user.getUsers().getInfo_man(), style);
            createCell(row, columnCount++, user.getUsers().getChildrenInfo(), style);
            createCell(row, columnCount++, user.getUsers().getChildAge(), style);
            createCell(row, columnCount++, user.getHelpType().toString(), style);
            createCell(row, columnCount++, user.getUsers().getDescription(), style);
            createCell(row, columnCount++, user.getDate(), style);
            createCell(row, columnCount++, user.getAdmin(), style);


        }
    }

    public void export(String a) throws IOException {
        writeHeaderLine();
        writeDataLines();

        File compressFile = null;
        File file = new File("src\\main\\resources\\exsell\\"+a+".xlsx");
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();

    }


}

//    Excell excell = new Excell(all);
//   excell.export(text);