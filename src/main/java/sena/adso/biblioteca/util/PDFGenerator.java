package sena.adso.biblioteca.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import sena.adso.biblioteca.dto.Prestamo;
import sena.adso.biblioteca.dto.Libro;
import sena.adso.biblioteca.dto.Lector;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Generador de documentos PDF para la biblioteca
 * @author ADSO
 */
public class PDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Genera un comprobante de préstamo en PDF
     * @param prestamo datos del préstamo
     * @param response respuesta HTTP
     * @throws DocumentException
     * @throws IOException
     */
    public static void generarComprobantePrestamo(Prestamo prestamo, HttpServletResponse response) 
            throws DocumentException, IOException {
        
        // Configurar respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=comprobante_prestamo_" + prestamo.getId() + ".pdf");

        // Crear documento
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        Paragraph title = new Paragraph("COMPROBANTE DE PRÉSTAMO", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Información de la biblioteca
        Paragraph biblioteca = new Paragraph("Sistema de Gestión de Biblioteca", HEADER_FONT);
        biblioteca.setAlignment(Element.ALIGN_CENTER);
        biblioteca.setSpacingAfter(30);
        document.add(biblioteca);

        // Tabla de información del préstamo
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Información del préstamo
        addTableRow(table, "Número de Préstamo:", String.valueOf(prestamo.getId()));
        addTableRow(table, "Fecha de Préstamo:", DATE_FORMAT.format(prestamo.getFechaPrestamo()));
        addTableRow(table, "Fecha de Devolución Esperada:", DATE_FORMAT.format(prestamo.getFechaDevolucionEsperada()));
        
        // Información del libro
        addTableHeader(table, "INFORMACIÓN DEL LIBRO");
        addTableRow(table, "Título:", prestamo.getLibroTitulo());
        addTableRow(table, "Autor:", prestamo.getLibroAutor());
        
        // Información del lector
        addTableHeader(table, "INFORMACIÓN DEL LECTOR");
        addTableRow(table, "Nombre:", prestamo.getLectorNombre());
        addTableRow(table, "Documento:", prestamo.getLectorDocumento());
        
        // Información del bibliotecario
        addTableHeader(table, "BIBLIOTECARIO RESPONSABLE");
        addTableRow(table, "Nombre:", prestamo.getBibliotecarioNombre());

        document.add(table);

        // Observaciones
        if (prestamo.getObservaciones() != null && !prestamo.getObservaciones().trim().isEmpty()) {
            Paragraph observaciones = new Paragraph("Observaciones: " + prestamo.getObservaciones(), NORMAL_FONT);
            observaciones.setSpacingBefore(20);
            document.add(observaciones);
        }

        // Términos y condiciones
        Paragraph terminos = new Paragraph("\nTérminos y Condiciones:", HEADER_FONT);
        document.add(terminos);
        
        List<String> condiciones = List.of(
            "• El libro debe ser devuelto en la fecha indicada",
            "• En caso de retraso se aplicará una multa diaria",
            "• El libro debe ser devuelto en las mismas condiciones",
            "• En caso de pérdida o daño, deberá reponerlo"
        );
        
        for (String condicion : condiciones) {
            Paragraph p = new Paragraph(condicion, NORMAL_FONT);
            document.add(p);
        }

        // Firma
        Paragraph firma = new Paragraph("\n\n_____________________\nFirma del Lector", NORMAL_FONT);
        firma.setAlignment(Element.ALIGN_RIGHT);
        document.add(firma);

        document.close();
    }

    /**
     * Genera un reporte de préstamos activos
     * @param prestamos lista de préstamos activos
     * @param response respuesta HTTP
     * @throws DocumentException
     * @throws IOException
     */
    public static void generarReportePrestamosActivos(List<Prestamo> prestamos, HttpServletResponse response) 
            throws DocumentException, IOException {
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_prestamos_activos.pdf");

        Document document = new Document(PageSize.A4.rotate()); // Horizontal para más columnas
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        Paragraph title = new Paragraph("REPORTE DE PRÉSTAMOS ACTIVOS", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Fecha del reporte
        Paragraph fecha = new Paragraph("Fecha del reporte: " + DATE_FORMAT.format(new java.util.Date()), NORMAL_FONT);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        // Tabla de préstamos
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 1});

        // Headers
        addTableCell(table, "ID", HEADER_FONT, Element.ALIGN_CENTER);
        addTableCell(table, "Libro", HEADER_FONT, Element.ALIGN_CENTER);
        addTableCell(table, "Lector", HEADER_FONT, Element.ALIGN_CENTER);
        addTableCell(table, "Préstamo", HEADER_FONT, Element.ALIGN_CENTER);
        addTableCell(table, "Devolución", HEADER_FONT, Element.ALIGN_CENTER);
        addTableCell(table, "Estado", HEADER_FONT, Element.ALIGN_CENTER);

        // Datos
        for (Prestamo prestamo : prestamos) {
            addTableCell(table, String.valueOf(prestamo.getId()), NORMAL_FONT, Element.ALIGN_CENTER);
            addTableCell(table, prestamo.getLibroTitulo(), NORMAL_FONT, Element.ALIGN_LEFT);
            addTableCell(table, prestamo.getLectorNombre(), NORMAL_FONT, Element.ALIGN_LEFT);
            addTableCell(table, DATE_FORMAT.format(prestamo.getFechaPrestamo()), NORMAL_FONT, Element.ALIGN_CENTER);
            addTableCell(table, DATE_FORMAT.format(prestamo.getFechaDevolucionEsperada()), NORMAL_FONT, Element.ALIGN_CENTER);
            
            String estado = prestamo.isVencido() ? "VENCIDO" : "ACTIVO";
            Font estadoFont = prestamo.isVencido() ? 
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.RED) : NORMAL_FONT;
            addTableCell(table, estado, estadoFont, Element.ALIGN_CENTER);
        }

        document.add(table);

        // Total
        Paragraph total = new Paragraph("Total de préstamos activos: " + prestamos.size(), HEADER_FONT);
        total.setSpacingBefore(20);
        document.add(total);

        document.close();
    }

    /**
     * Agrega una fila a la tabla con título y valor
     */
    private static void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    /**
     * Agrega un header que ocupa toda la fila
     */
    private static void addTableHeader(PdfPTable table, String header) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, HEADER_FONT));
        headerCell.setColspan(2);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPadding(10);
        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(headerCell);
    }

    /**
     * Agrega una celda a la tabla con formato específico
     */
    private static void addTableCell(PdfPTable table, String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        table.addCell(cell);
    }
}