# 🎓 YOESTUD.IO - Estado del Proyecto

## 🧠 Identidad del Proyecto
Asistente de desarrollo para YOESTUD.IO, aplicación móvil Android para estudiantes universitarios.
- **Equipo:** Benjamín Belmar (PO), Matías Sanhueza (SM), Marcel Droguett (Backend), Belén Alarcón (Frontend).

## 🛠️ Stack Tecnológico Actual
- **Backend:** Spring Boot 3.4.1 (Java 21).
- **IA:** Google Gemini (Modelo: `gemini-flash-latest`) vía HTTP Directo.
- **DB Relacional:** NeonDB (PostgreSQL) - Conectada y funcional.
- **DB No Relacional:** MongoDB - Configurada (lista para usar).

## ✅ Avances realizados (Sprint 1.1)
1. **Limpieza:** Se eliminó el paquete `com.example.demo` y se unificó todo bajo `com.yoestudio`.
2. **IA Funcional:** El asistente responde correctamente desde el endpoint `/api/ai/chat`.
3. **Interfaz de Prueba:** Accesible en `http://localhost:8080/index.html`.
4. **Seguridad:** Deshabilitada temporalmente para facilitar el desarrollo (`SecurityConfig.permitAll()`).
5. **Protección de Credenciales:** `apis.txt` está en `.gitignore` y fuera del historial de Git.

## 📌 Reglas de Trabajo (Mandatorias)
- **Nomenclatura:** Variables de negocio en ESPAÑOL (usuario, pago, material), nombres técnicos en INGLÉS (Service, Controller, Repository).
- **Cabeceras:** Todo archivo Java debe iniciar con: *Nombre del archivo, Propósito y Fecha*.
- **Comentarios:** Breves y técnicos. Evitar lenguaje "tipo IA" (ej: nada de "hemos adaptado", "forzamos").
- **Avanzar de a un paso:** Esperar confirmación ("listo" o "siguiente") antes de continuar.

## 🚀 Próximo Paso: Sprint 1.2
- **US01 & US02:** Registro e Inicio de Sesión.
- Implementar seguridad real con **JWT** en el paquete `seguridad`.
- Vincular la tabla `usuarios` y `roles` de NeonDB.

---
*Estado: Base del proyecto sólida y lista para escalado.*
