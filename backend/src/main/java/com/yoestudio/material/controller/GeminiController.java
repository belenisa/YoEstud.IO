
package com.yoestudio.material.controller;

import com.yoestudio.material.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String mensaje) {
        return ResponseEntity.ok(geminiService.generarRespuesta(mensaje));
    }

    @PostMapping("/estudiar")
    public ResponseEntity<String> estudiar(@RequestBody String texto) {
        return ResponseEntity.ok(geminiService.generarCuestionario(texto));
    }

    @PostMapping("/analizar-archivo")
    public ResponseEntity<String> analizarArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("mensaje") String mensaje) {
        return ResponseEntity.ok(geminiService.analizarDocumento(archivo, mensaje));
    }

    @PostMapping("/descargar-pdf")
    public ResponseEntity<byte[]> descargarPdf(@RequestBody String texto) {
        byte[] pdf = geminiService.generarPdfDesdeTexto(texto);
        if (pdf == null) return ResponseEntity.internalServerError().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Prueba_YoEstudio.pdf");
        
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
