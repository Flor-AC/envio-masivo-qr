package com.flor.qr;

// Importamos clases de Java para manejar archivos, listas y mapas.
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

//Clase encargada de la persistencia de datos desde una fuente Excel
//Objetivo: transformar filas de una hoja de cálculo en una lista de objetos tipo Cliente
public class ExcelReader {

    //Lee un archivo excel y extrae los clientes marcados como activos
    public static List<Cliente> leerClientes(String rutaExcel) throws Exception {
        List<Cliente> clientes = new ArrayList<>();
        //DataFormatter -> connvierte cualquier tipo de celda (numerica, fecha, etc) a una representación en texto tal cual se ve en el excel
        DataFormatter formatter = new DataFormatter(); // Herramienta para leer celdas como texto

        //Usamos try-with-resources para asegurar que el flujo de datos (fis) y el libro (workbook) se cierren automaticamente al finalizar, incluso si hay error
        try (FileInputStream fis = new FileInputStream(new File(rutaExcel));
            Workbook workbook = WorkbookFactory.create(fis)) {
            
            //Accedemos a la primera pestaña del libro (incide 0)
            Sheet sheet = workbook.getSheetAt(0);
            //El iterator nos permite recorrer las filas en una por una sin conocer el total
            Iterator<Row> rows = sheet.iterator();

            //Validación defensiva, si el archivo no tiene ni una sola fila terminamos la ejecución
            if (!rows.hasNext()) return clientes;

            // 1. Identificar en qué columna está cada dato
            Row header = rows.next(); //tomamos la primera fila 
            Map<String, Integer> columnas = new HashMap<>();
            for (Cell cell : header) {
                //Guardamos el nombre en minusculas para una comparación más flexible
                String nombreCol = cell.getStringCellValue().trim().toLowerCase();
                columnas.put(nombreCol, cell.getColumnIndex());
            }

            // 2. Procesar las filas de datos
            while (rows.hasNext()) {
                Row row = rows.next();
                
                // Verificamos si la fila está marcada como "VERDADERO" en la columna 'activo'
                String activo = getCellValue(row, columnas.get("activo"), formatter);
                
                //Solo procesamos si la celda dice "VERDADERO" (ignora mayúsculas)
                if (activo.equalsIgnoreCase("verdadero")) {
                    //Extraemos los datos necesarios usando el mapa de índices creado arriba
                    String nombre = getCellValue(row, columnas.get("cliente"), formatter);
                    String referencia = getCellValue(row, columnas.get("referencia"), formatter);
                    String correo = getCellValue(row, columnas.get("cliente/correo electrónico"), formatter);

                    // Agregamos a la lista solo si tiene los datos esenciales
                    if (!correo.isEmpty()) {
                        clientes.add(new Cliente(nombre.trim(), correo.trim(), referencia.trim()));
                    }
                }
            }
        }
        //Al salir del bloque try, los recursos se liberan automaticamente
        return clientes;
    }

    // Método de apoyo para leer celdas sin que el programa "explote" si están vacías
    private static String getCellValue(Row row, Integer index, DataFormatter formatter) {
        //Si la columna no se encontró en el encabezado, devolvemos vacío
        if (index == null) return "";
        //Obtenemos el objeto celda
        Cell cell = row.getCell(index);
        //Formateamos la celda a String (maneja nulos internamente devolviendo "")
        return formatter.formatCellValue(cell);
    }
}