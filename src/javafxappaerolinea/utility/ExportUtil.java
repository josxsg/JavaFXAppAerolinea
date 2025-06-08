package javafxappaerolinea.utility;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Chunk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;


public class ExportUtil {
    
    
    public static <T> void exportToCSV(List<T> data, String filePath) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }
        
        try (Writer writer = new FileWriter(filePath)) {
            Class<?> clazz = data.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();
            List<String> headers = new ArrayList<>();
            
            for (Field field : fields) {
                field.setAccessible(true);
                if (!List.class.isAssignableFrom(field.getType())) {
                    headers.add(field.getName());
                }
            }
            
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            
            csvWriter.writeNext(headers.toArray(new String[0]));
            
            for (T item : data) {
                List<String> rowData = new ArrayList<>();
                for (String header : headers) {
                    try {
                        Field field = clazz.getDeclaredField(header);
                        field.setAccessible(true);
                        Object value = field.get(item);
                        rowData.add(value != null ? value.toString() : "");
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        rowData.add("");
                    }
                }
                csvWriter.writeNext(rowData.toArray(new String[0]));
            }
            
            csvWriter.flush();
        } catch (Exception e) {
            throw new IOException("Error al exportar a CSV: " + e.getMessage(), e);
        }
    }
    
    public static <T> void exportToCSV(List<T> data, String filePath, List<String> columnOrder) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }

        try (Writer writer = new FileWriter(filePath)) {
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            csvWriter.writeNext(columnOrder.toArray(new String[0]));

            for (T item : data) {
                List<String> rowData = new ArrayList<>();
                for (String column : columnOrder) {
                    try {
                        Object value = getFieldValue(item, column);
                        rowData.add(value != null ? value.toString() : "");
                    } catch (Exception e) {
                        rowData.add("");
                    }
                }
                csvWriter.writeNext(rowData.toArray(new String[0]));
            }

            csvWriter.flush();
        } catch (Exception e) {
            throw new IOException("Error al exportar a CSV: " + e.getMessage(), e);
        }
    }

    public static <T> void exportToXLS(List<T> data, String filePath, String sheetName) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }
        
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            Class<?> clazz = data.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();
            List<String> headers = new ArrayList<>();
            
            for (Field field : fields) {
                field.setAccessible(true);
                if (!List.class.isAssignableFrom(field.getType())) {
                    headers.add(field.getName());
                }
            }
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    try {
                        Field field = clazz.getDeclaredField(headers.get(i));
                        field.setAccessible(true);
                        Object value = field.get(item);
                        
                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else if (value instanceof Date) {
                            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        cell.setCellValue("");
                    }
                }
            }
            
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            throw new IOException("Error al exportar a XLS: " + e.getMessage(), e);
        }
    }
    
    public static <T> void exportToXLS(List<T> data, String filePath, String sheetName, List<String> columnOrder) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }

        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnOrder.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnOrder.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < columnOrder.size(); i++) {
                    Cell cell = row.createCell(i);
                    try {
                        Object value = getFieldValue(item, columnOrder.get(i));

                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else if (value instanceof Date) {
                            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } catch (Exception e) {
                        cell.setCellValue("");
                    }
                }
            }
            for (int i = 0; i < columnOrder.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            throw new IOException("Error al exportar a XLS: " + e.getMessage(), e);
        }
    }

    public static <T> void exportToXLSX(List<T> data, String filePath, String sheetName) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            Class<?> clazz = data.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();
            List<String> headers = new ArrayList<>();
            
            for (Field field : fields) {
                field.setAccessible(true);
                if (!List.class.isAssignableFrom(field.getType())) {
                    headers.add(field.getName());
                }
            }
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    try {
                        Field field = clazz.getDeclaredField(headers.get(i));
                        field.setAccessible(true);
                        Object value = field.get(item);
                        
                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else if (value instanceof Date) {
                            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        cell.setCellValue("");
                    }
                }
            }
            
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            throw new IOException("Error al exportar a XLSX: " + e.getMessage(), e);
        }
    }
    
    public static <T> void exportToXLSX(List<T> data, String filePath, String sheetName, List<String> columnOrder) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnOrder.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnOrder.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < columnOrder.size(); i++) {
                    Cell cell = row.createCell(i);
                    try {
                        Object value = getFieldValue(item, columnOrder.get(i));

                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else if (value instanceof Date) {
                            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } catch (Exception e) {
                        cell.setCellValue("");
                    }
                }
            }

            for (int i = 0; i < columnOrder.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            throw new IOException("Error al exportar a XLSX: " + e.getMessage(), e);
        }
    }
    
   
    public static <T> void exportToPDF(List<T> data, String filePath, String title) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);
            document.add(Chunk.NEWLINE);
            
            Class<?> clazz = data.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();
            List<String> headers = new ArrayList<>();
            
            for (Field field : fields) {
                field.setAccessible(true);
                if (!List.class.isAssignableFrom(field.getType())) {
                    headers.add(field.getName());
                }
            }
            
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);
            
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new java.awt.Color(220, 220, 220));
                table.addCell(cell);
            }
            
            for (T item : data) {
                for (String header : headers) {
                    try {
                        Field field = clazz.getDeclaredField(header);
                        field.setAccessible(true);
                        Object value = field.get(item);
                        String cellValue = value != null ? value.toString() : "";
                        
                        if (value instanceof Date) {
                            cellValue = new SimpleDateFormat("yyyy-MM-dd").format((Date) value);
                        }
                        
                        table.addCell(cellValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        table.addCell("");
                    }
                }
            }
            
            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Error al exportar a PDF: " + e.getMessage(), e);
        }
    }
    
    public static <T> void exportToPDF(List<T> data, String filePath, String title, List<String> columnOrder) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(columnOrder.size());
            table.setWidthPercentage(100);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            for (String header : columnOrder) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new java.awt.Color(220, 220, 220));
                table.addCell(cell);
            }

            for (T item : data) {
                for (String column : columnOrder) {
                    try {
                        Object value = getFieldValue(item, column);
                        String cellValue = value != null ? value.toString() : "";

                        if (value instanceof Date) {
                            cellValue = new SimpleDateFormat("yyyy-MM-dd").format((Date) value);
                        }

                        table.addCell(cellValue);
                    } catch (Exception e) {
                        table.addCell("");
                    }
                }
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Error al exportar a PDF: " + e.getMessage(), e);
        }
    }
    
    public static <T> void exportToJSON(List<T> data, String filePath) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista de datos no puede estar vacía");
        }
        
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            gson.toJson(data, writer);
        } catch (Exception e) {
            throw new IOException("Error al exportar a JSON: " + e.getMessage(), e);
        }
    }
    
    private static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            }
            throw e;
        }
    }
}