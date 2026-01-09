package com.flor.qr;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {

    public static List<Cliente> leerClientes(String rutaExcel) throws Exception {
        List<Cliente> clientes = new ArrayList<>();

        FileInputStream fis = new FileInputStream(new File(rutaExcel));
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();
        Row header = rows.next();

        Map<String, Integer> columnas = new HashMap<>();
        for (Cell cell : header) {
            columnas.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
        }

        while (rows.hasNext()) {
            Row row = rows.next();

            String activo = row.getCell(columnas.get("activo")).getStringCellValue();
            if (!activo.equalsIgnoreCase("verdadero")) {
                continue; // no se envía
            }

            String nombre = row.getCell(columnas.get("cliente")).getStringCellValue();
            String referencia = row.getCell(columnas.get("referencia")).getStringCellValue();
            String correo = row.getCell(columnas.get("cliente/correo electrónico")).getStringCellValue();

            clientes.add(new Cliente(
                    nombre.trim(),
                    correo.trim(),
                    referencia.trim()
            ));
        }

        workbook.close();
        return clientes;
    }
}
