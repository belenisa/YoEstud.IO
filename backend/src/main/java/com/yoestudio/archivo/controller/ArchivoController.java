package com.yoestudio.archivo.controller;

import com.yoestudio.archivo.model.ArchivoGenerado;
import com.yoestudio.archivo.service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @Autowired
    private ArchivoService archivoService;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable String id) {
        try {
            ArchivoGenerado archivo = archivoService.obtenerPorId(id);
            if (archivo == null) return ResponseEntity.notFound().build();

            Path filePath = Paths.get(archivo.getRutaServidor());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getNombreArchivo() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ArchivoGenerado> listarPorUsuario(@PathVariable Long usuarioId) {
        return archivoService.listarPorUsuario(usuarioId);
    }
}
