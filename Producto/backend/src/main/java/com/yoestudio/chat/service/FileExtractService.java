package com.yoestudio.chat.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Collectors;

@Service
public class FileExtractService {

    public String extraerTexto(String nombreArchivo, byte[] contenido) throws Exception {
        String extension = getExtension(nombreArchivo);
        
        switch (extension.toLowerCase()) {
            case "pdf":
                return extraerPdf(contenido);
            case "doc":
                return extraerDoc(contenido);
            case "docx":
                return extraerDocx(contenido);
            default:
                throw new IllegalArgumentException("Tipo de archivo no soportado: " + extension + ". Solo se permiten PDF, DOC y DOCX.");
        }
    }

    private String extraerPdf(byte[] contenido) throws Exception {
        try (PDDocument document = Loader.loadPDF(contenido)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extraerDoc(byte[] contenido) throws Exception {
        try (InputStream is = new ByteArrayInputStream(contenido);
             HWPFDocument doc = new HWPFDocument(is);
             WordExtractor extractor = new WordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private String extraerDocx(byte[] contenido) throws Exception {
        try (InputStream is = new ByteArrayInputStream(contenido);
             XWPFDocument doc = new XWPFDocument(is)) {
            return doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining("\n"));
        }
    }

    private String getExtension(String nombre) {
        int index = nombre.lastIndexOf(".");
        if (index == -1) return "";
        return nombre.substring(index + 1);
    }
}
