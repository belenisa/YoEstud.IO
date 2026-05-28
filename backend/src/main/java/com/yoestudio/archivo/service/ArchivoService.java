package com.yoestudio.archivo.service;

import com.yoestudio.archivo.model.ArchivoGenerado;
import com.yoestudio.archivo.repository.ArchivoRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class ArchivoService {

    @Autowired
    private ArchivoRepository archivoRepository;

    @Value("${app.output.dir:./output}")
    private String outputDir;

    public ArchivoGenerado guardarArchivo(Long usuarioId, String nombre, String contenido) {
        try {
            String userDirPath = outputDir + "/" + usuarioId;
            Files.createDirectories(Paths.get(userDirPath));

            String baseName = nombre.contains(".") ? nombre.substring(0, nombre.indexOf('.')) : nombre;
            String nombrePdf = baseName + ".pdf";

            Path filePath = Paths.get(userDirPath, nombrePdf);

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                float margin = 50;
                float lineHeight = 14;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;

                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                contentStream.newLineAtOffset(margin, yPosition);

                String[] lines = contenido.split("\n", -1);
                for (int i = 0; i < lines.length; i++) {
                    if (i > 0) {
                        contentStream.newLineAtOffset(0, -lineHeight);
                        yPosition -= lineHeight;
                    }

                    if (yPosition < margin) {
                        contentStream.endText();
                        contentStream.close();

                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.beginText();
                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                        contentStream.newLineAtOffset(margin, yStart);
                        yPosition = yStart;
                    }

                    contentStream.showText(sanitizarTexto(lines[i]));
                }

                contentStream.endText();
                contentStream.close();

                document.save(filePath.toFile());
            }

            ArchivoGenerado archivo = new ArchivoGenerado();
            archivo.setUsuarioId(usuarioId);
            archivo.setNombreArchivo(nombrePdf);
            archivo.setRutaServidor(filePath.toString());
            archivo.setTipo("prueba");
            archivo.setFechaCreacion(new Date());
            archivo.setEstado("listo");

            return archivoRepository.save(archivo);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar archivo PDF: " + e.getMessage());
        }
    }

    public ArchivoGenerado obtenerPorId(String id) {
        return archivoRepository.findById(id).orElse(null);
    }

    public List<ArchivoGenerado> listarPorUsuario(Long usuarioId) {
        return archivoRepository.findByUsuarioId(usuarioId);
    }

    private String sanitizarTexto(String texto) {
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '\u02B0': case '\u02B1': case '\u02B2': case '\u02B3': case '\u02B4':
                case '\u02B5': case '\u02B6': case '\u02B7': case '\u02B8':
                    sb.append((char)(c - 0x02B0 + 'h')); break;
                case '\u02E3': sb.append('x'); break;
                case '\u1D2C': case '\u1D2D': case '\u1D2E': case '\u1D2F':
                case '\u1D30': case '\u1D31': case '\u1D32': case '\u1D33':
                case '\u1D34': case '\u1D35': case '\u1D36': case '\u1D37':
                case '\u1D38': case '\u1D39': case '\u1D3A':
                    sb.append(' '); break;
                case '\u221A': sb.append("raiz("); break;
                case '\u2248': sb.append('~'); break;
                case '\u2260': sb.append("!="); break;
                case '\u2264': sb.append("<="); break;
                case '\u2265': sb.append(">="); break;
                case '\u03C0': sb.append("pi"); break;
                case '\u0394': sb.append("Delta"); break;
                case '\u03A3': sb.append("Sigma"); break;
                case '\u2190': sb.append("<--"); break;
                case '\u2191': sb.append("sube"); break;
                case '\u2192': sb.append("->"); break;
                case '\u2193': sb.append("baja"); break;
                case '\u2194': sb.append("<->"); break;
                case '\u00B9': sb.append("^1"); break;
                case '\u2070': sb.append("^0"); break;
                case '\u2074': sb.append("^4"); break;
                case '\u2075': sb.append("^5"); break;
                case '\u2076': sb.append("^6"); break;
                case '\u2077': sb.append("^7"); break;
                case '\u2078': sb.append("^8"); break;
                case '\u2079': sb.append("^9"); break;
                case '\u207A': sb.append("^+"); break;
                case '\u207B': sb.append("^-"); break;
                default:
                    if (c <= 255) {
                        sb.append(c);
                    } else {
                        sb.append(' ');
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
