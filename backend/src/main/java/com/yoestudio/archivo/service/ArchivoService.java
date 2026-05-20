package com.yoestudio.archivo.service;

import com.yoestudio.archivo.model.ArchivoGenerado;
import com.yoestudio.archivo.repository.ArchivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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

            Path filePath = Paths.get(userDirPath, nombre);
            Files.write(filePath, contenido.getBytes());

            ArchivoGenerado archivo = new ArchivoGenerado();
            archivo.setUsuarioId(usuarioId);
            archivo.setNombreArchivo(nombre);
            archivo.setRutaServidor(filePath.toString());
            archivo.setTipo("prueba");
            archivo.setFechaCreacion(new Date());
            archivo.setEstado("listo");

            return archivoRepository.save(archivo);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar archivo: " + e.getMessage());
        }
    }

    public ArchivoGenerado obtenerPorId(String id) {
        return archivoRepository.findById(id).orElse(null);
    }

    public List<ArchivoGenerado> listarPorUsuario(Long usuarioId) {
        return archivoRepository.findByUsuarioId(usuarioId);
    }
}
