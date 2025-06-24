package sena.adso.biblioteca.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sena.adso.biblioteca.dto.Libro;
import sena.adso.biblioteca.dto.Prestamo;
import sena.adso.biblioteca.dto.Lector;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Generador de reportes en Excel para la biblioteca
 * @author ADSO
 */
public class ExcelGenerator {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Genera un reporte de inventario de libros en Excel
     * @param libros lista de libros
     * @param response respuesta HTTP
     * @throws IOException
     */
    public static void generarReporteInventario(List<Libro> libros, HttpServletResponse response) 
            throws IOException {
        
        // Configurar respuesta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventario_libros.xlsx");

        // Crear workbook y hoja
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventario de Libros");

        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        // Headers
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "ID", "Título", "Autor", "Editorial", "Año", 
            "Categoría", "ISBN", "Ubicación", "Total", 
            "Disponible", "Estado"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 1;
        for (Libro libro : libros) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(libro.getId());
            row.createCell(1).setCellValue(libro.getTitulo());
            row.createCell(2).setCellValue(libro.getAutor());
            row.createCell(3).setCellValue(libro.getEditorial());
            
            Cell yearCell = row.createCell(4);
            yearCell.setCellValue(libro.getAnoPublicacion());
            yearCell.setCellStyle(numberStyle);
            
            row.createCell(5).setCellValue(libro.getCategoriaNombre());
            row.createCell(6).setCellValue(libro.getIsbn() != null ? libro.getIsbn() : "");
            row.createCell(7).setCellValue(libro.getUbicacion() != null ? libro.getUbicacion() : "");
            
            Cell totalCell = row.createCell(8);
            totalCell.setCellValue(libro.getCantidadTotal());
            totalCell.setCellStyle(numberStyle);
            
            Cell disponibleCell = row.createCell(9);
            disponibleCell.setCellValue(libro.getCantidadDisponible());
            disponibleCell.setCellStyle(numberStyle);
            
            row.createCell(10).setCellValue(libro.getEstado());

            // Aplicar estilo a las celdas de datos
            for (int i = 0; i < headers.length; i++) {
                if (i != 4 && i != 8 && i != 9) { // Excepto las celdas numéricas
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir y cerrar
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * Genera un reporte de préstamos en Excel
     * @param prestamos lista de préstamos
     * @param response respuesta HTTP
     * @throws IOException
     */
    public static void generarReportePrestamos(List<Prestamo> prestamos, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_prestamos.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Préstamos");

        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Headers
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "ID", "Libro", "Autor", "Lector", "Documento", 
            "Fecha Préstamo", "Fecha Devolución Esperada", 
            "Fecha Devolución Real", "Estado", "Bibliotecario"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 1;
        for (Prestamo prestamo : prestamos) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(prestamo.getId());
            row.createCell(1).setCellValue(prestamo.getLibroTitulo());
            row.createCell(2).setCellValue(prestamo.getLibroAutor());
            row.createCell(3).setCellValue(prestamo.getLectorNombre());
            row.createCell(4).setCellValue(prestamo.getLectorDocumento());
            
            // Fechas
            Cell fechaPrestamoCell = row.createCell(5);
            fechaPrestamoCell.setCellValue(prestamo.getFechaPrestamo());
            fechaPrestamoCell.setCellStyle(dateStyle);
            
            Cell fechaEsperadaCell = row.createCell(6);
            fechaEsperadaCell.setCellValue(prestamo.getFechaDevolucionEsperada());
            fechaEsperadaCell.setCellStyle(dateStyle);
            
            Cell fechaRealCell = row.createCell(7);
            if (prestamo.getFechaDevolucionReal() != null) {
                fechaRealCell.setCellValue(prestamo.getFechaDevolucionReal());
                fechaRealCell.setCellStyle(dateStyle);
            } else {
                fechaRealCell.setCellValue("");
                fechaRealCell.setCellStyle(dataStyle);
            }
            
            row.createCell(8).setCellValue(prestamo.getEstado());
            row.createCell(9).setCellValue(prestamo.getBibliotecarioNombre());

            // Aplicar estilo a las celdas de datos (excepto fechas)
            for (int i = 0; i < headers.length; i++) {
                if (i != 5 && i != 6 && i != 7) { // Excepto las celdas de fecha
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * Genera un reporte de lectores en Excel
     * @param lectores lista de lectores
     * @param response respuesta HTTP
     * @throws IOException
     */
    public static void generarReporteLectores(List<Lector> lectores, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_lectores.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte de Lectores");

        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Headers
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "ID", "Nombres", "Apellidos", "Documento", 
            "Email", "Teléfono", "Dirección", 
            "Fecha Nacimiento", "Estado", "Fecha Registro"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 1;
        for (Lector lector : lectores) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(lector.getId());
            row.createCell(1).setCellValue(lector.getNombres());
            row.createCell(2).setCellValue(lector.getApellidos());
            row.createCell(3).setCellValue(lector.getDocumento());
            row.createCell(4).setCellValue(lector.getEmail() != null ? lector.getEmail() : "");
            row.createCell(5).setCellValue(lector.getTelefono() != null ? lector.getTelefono() : "");
            row.createCell(6).setCellValue(lector.getDireccion() != null ? lector.getDireccion() : "");
            
            // Fechas
            Cell fechaNacimientoCell = row.createCell(7);
            if (lector.getFechaNacimiento() != null) {
                fechaNacimientoCell.setCellValue(lector.getFechaNacimiento());
                fechaNacimientoCell.setCellStyle(dateStyle);
            } else {
                fechaNacimientoCell.setCellValue("");
                fechaNacimientoCell.setCellStyle(dataStyle);
            }
            
            row.createCell(8).setCellValue(lector.getEstado());
            
            Cell fechaRegistroCell = row.createCell(9);
            if (lector.getFechaRegistro() != null) {
                fechaRegistroCell.setCellValue(lector.getFechaRegistro());
                fechaRegistroCell.setCellStyle(dateStyle);
            }

            // Aplicar estilo a las celdas de datos (excepto fechas)
            for (int i = 0; i < headers.length; i++) {
                if (i != 7 && i != 9) { // Excepto las celdas de fecha
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Métodos para crear estilos
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }
}