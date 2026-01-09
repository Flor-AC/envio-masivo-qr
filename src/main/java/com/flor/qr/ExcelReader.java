package com.flor.qr;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

    public static List<Cliente> leerClientes(String rutaExcel) throws Exception {
        List<Cliente> clientes = new ArrayList<>();
        DataFormatter formatter = new DataFormatter(); 

        try (FileInputStream fis = new FileInputStream(new File(rutaExcel));
            Workbook workbook = WorkbookFactory.create(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) return clientes;

            // 1. Mapeo de columnas
            Row header = rows.next(); 
            Map<String, Integer> columnas = new HashMap<>();
            for (Cell cell : header) {
                String nombreCol = cell.getStringCellValue().trim().toLowerCase();
                columnas.put(nombreCol, cell.getColumnIndex());
            }

            // 2. Procesar las filas
            while (rows.hasNext()) {
                Row row = rows.next();
                
                // --- MEJORA AQUÍ: Detección robusta de 'Activo' ---
                String activo = getCellValue(row, columnas.get("activo"), formatter).trim();
                
                // Comparamos contra "VERDADERO" y también contra "TRUE" por si el Excel está en inglés internamente
                if (activo.equalsIgnoreCase("verdadero") || activo.equalsIgnoreCase("true")) {
                    
                    String nombre = getCellValue(row, columnas.get("cliente"), formatter);
                    String referencia = getCellValue(row, columnas.get("referencia"), formatter);
                    String correo = getCellValue(row, columnas.get("cliente/correo electrónico"), formatter);

                    if (!correo.isEmpty() && correo.contains("@")) {
                        clientes.add(new Cliente(nombre.trim(), correo.trim(), referencia.trim()));
                    }
                }
            }
        }
        return clientes;
    }

    private static String getCellValue(Row row, Integer index, DataFormatter formatter) {
        if (index == null) return "";
        Cell cell = row.getCell(index);
        return formatter.formatCellValue(cell);
    }
}