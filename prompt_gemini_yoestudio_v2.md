# Prompt maestro para Gemini — Proyecto YOESTUD.IO (v2)

> Copia y pega este prompt completo al inicio de cada sesión con Gemini.
> Cada vez que termines una tarea, dile a Gemini: "Avanza al siguiente paso."

---

## 🧠 CONTEXTO DEL PROYECTO

Eres el asistente de desarrollo del proyecto **YOESTUD.IO**, una aplicación móvil Android para estudiantes universitarios. Equipo Scrum:

- **Product Owner:** Benjamín Belmar
- **Scrum Master:** Matías Sanhueza
- **Backend Developer:** Marcel Droguett
- **Frontend Developer:** Belén Alarcón

### ¿Qué es YOESTUD.IO?

App móvil nativa Android que permite a estudiantes:
1. Subir, buscar y descargar material de estudio clasificado por asignatura.
2. Calificar y comentar material (lógica tipo red social).
3. Sesiones de estudio focalizadas (temporizador + reducción de distracciones).
4. **Chat con IA** (Gemini API) para resolver dudas académicas y generar archivos de prueba.

---

## 🏗️ ARQUITECTURA DEL SISTEMA

El sistema tiene DOS componentes separados que se comunican por HTTP:

```
[PC del desarrollador]                    [Teléfono Android]
┌─────────────────────────────┐           ┌─────────────────────┐
│  Servidor Spring Boot       │◄─────────►│  App Android        │
│  - API REST (puerto 8080)   │   HTTP/   │  (Kotlin + MVVM)    │
│  - Conecta a NeonDB         │   JSON    │  - Retrofit2        │
│  - Conecta a MongoDB        │           │  - Gemini API call  │
│  - Llama a Gemini API       │           │                     │
│  - Genera archivos .pdf/.txt│           │  Descarga archivos  │
│    en /output del servidor  │           │  generados por IA   │
└─────────────────────────────┘           └─────────────────────┘
         ▲                                         │
         │                                    Descarga via
    NeonDB (PostgreSQL)                    GET /api/archivos/{id}
    MongoDB
    Gemini API
```

**Importante:** El servidor corre en la PC del developer. El teléfono se conecta al servidor por IP local (ej: `http://192.168.1.X:8080`). La URL base se configura en `BuildConfig` o en un archivo `config.properties` de la app.

---

## 🗄️ BASE DE DATOS — ESTADO ACTUAL

### NeonDB (PostgreSQL serverless) — tablas existentes

**`usuarios`**
| Campo | Tipo | Notas |
|---|---|---|
| id | bigserial PK | |
| nombre | varchar(150) | |
| email | varchar(150) | |
| password | varchar(255) | BCrypt |
| tipo | enum: FREE / PREMIUM | |
| rol_id | bigint FK → roles | |

**`roles`**
| id | nombre |
|---|---|
| 1 | ADMIN |
| 2 | ESTUDIANTE |

**`pagos`**
| Campo | Tipo |
|---|---|
| id | bigserial PK |
| usuario_id | bigint FK |
| monto | numeric(10,2) |
| fecha | timestamp with timezone |
| estado | varchar(50) |
| transaccion_externa_id | varchar |

### MongoDB — colecciones a crear

**`materiales`** (documento principal)
```json
{
  "_id": "ObjectId",
  "titulo": "string",
  "descripcion": "string",
  "asignatura": "string",
  "area_conocimiento": "string",
  "tipo_archivo": "string",
  "url_archivo": "string",
  "usuario_id": 1,
  "fecha_subida": "ISODate",
  "calificacion_promedio": 4.5,
  "calificaciones": [
    { "usuario_id": 2, "valor": 5, "comentario": "string", "fecha": "ISODate" }
  ]
}
```

**`mensajes_chat`** (historial del chat con IA)
```json
{
  "_id": "ObjectId",
  "usuario_id": 1,
  "sesion_id": "string",
  "mensajes": [
    { "rol": "user", "contenido": "string", "fecha": "ISODate" },
    { "rol": "assistant", "contenido": "string", "fecha": "ISODate" }
  ],
  "fecha_inicio": "ISODate"
}
```

**`archivos_generados`** (archivos creados por la IA)
```json
{
  "_id": "ObjectId",
  "usuario_id": 1,
  "nombre_archivo": "prueba_matematicas.pdf",
  "ruta_servidor": "/output/prueba_matematicas.pdf",
  "tipo": "prueba",
  "fecha_creacion": "ISODate",
  "estado": "listo"
}
```

---

## 🎨 DISEÑO VISUAL (del prototipo Figma)

La app tiene **modo claro y modo oscuro**. El modo oscuro es el principal (fondo `#0D1B2A`, azul eléctrico `#0000FF` para botones primarios).

### Paleta de colores
| Token | Modo Claro | Modo Oscuro |
|---|---|---|
| Background | `#B0C4D8` (azul grisáceo) | `#0D1B2A` (azul marino oscuro) |
| Surface (cards) | `#FFFFFF` | `#1A2B3C` |
| Primary (botones) | `#1A1A1A` (negro) | `#0000FF` (azul eléctrico) |
| Text primary | `#000000` | `#FFFFFF` |
| Text secondary | `#666666` | `#AAAAAA` |
| Accent (Cerrar sesión) | `#FF0000` | `#FF0000` |

### Pantallas y su estructura

#### 1. Login (`LoginActivity`)
- Fondo: color background
- Logo "YOESTUD.IO" centrado arriba (bold, grande)
- Ícono de usuario circular
- Card blanca/oscura con: campo "Nombre de usuario", campo "Contraseña", botón "Ingresar" (full width), link "¿Olvidaste tu contraseña?"
- Botón "Registrarse" fuera de la card, abajo

#### 2. Registro (`RegisterActivity`)
- Misma estructura que Login
- Card con: "Nombre de Usuario", "Correo Electrónico", "Contraseña", checkbox "Aceptar Términos y condiciones" + link "Leer los Términos y condiciones", botón "Registrarse"

#### 3. Home (`HomeFragment`)
- Header: ícono de menú lateral (☰) + texto "Bienvenido / [nombre usuario]"
- Barra de búsqueda redondeada con ícono ☰ adentro y lupa
- Sección "Historial" con card que lista materiales recientes del usuario (ej: "Ramo 1: Evaluación", "Ramo 2: Tarea 2")
- Sección "Configuración Rápida" con botón "Modo Concentración" y toggle "Modo Oscuro"

#### 4. Listado de material (`MaterialListFragment`)
- Barra de búsqueda igual que home
- Botón ← volver
- Lista de cards verticales, cada una con: imagen/ícono de documento (placeholder gris/oscuro), título en negrita, descripción en gris

#### 5. Detalle de material (`MaterialDetailFragment`)
- Muestra nombre y descripción completa del documento
- Botón "Descargar"
- Botón "Calificar"

#### 6. Calificación (`CalificarFragment`)
- Card con título "¿Cómo calificarías este documento?"
- 5 estrellas (☆☆☆☆☆), etiquetas "Insuficiente" / "Excelente"
- Card separada: "Comentarios / Observaciones" con `EditText` multilínea placeholder "Comenta (opcional)"
- Botón "Enviar" full width

#### 7. Drawer lateral (`NavigationDrawer`)
- Avatar circular + nombre de usuario (placeholder barra)
- Ítems con íconos: "Ver Ramos", "Subir Material de Estudio", "Documentos descargados", "YoEstud.io" (logo), "Chat con IA"
- Al fondo: toggle "Modo Oscuro", "Configuración", "Cerrar Sesión" (en rojo)

#### 8. Documentos descargados (`DescargasFragment`)
- Título "Documentos Descargados"
- Spinner/dropdown "Seleccionar Asignatura"
- Lista expandible por asignatura → muestra ítems (Tarea 1, Tarea 2, Evaluación…)

#### 9. Subir material (`SubirMaterialFragment`)
- Card con título "Subir Material de Estudio"
- Área grande con "+" para seleccionar archivo (file picker)
- Campo "Descripción" multilínea
- Botón "Subir Archivo"

#### 10. Chat con IA (`ChatIAFragment`) ← PRIORIDAD ACTUAL
- Fondo azul oscuro (`#0A1628`)
- Mensajes del usuario: globo blanco, alineado a la derecha
- Mensajes de la IA: globo gris azulado, alineado a la izquierda
- Input inferior con placeholder "Pregúntale a la IA...."
- Íconos en el input: imagen 🖼, código `<>`, micrófono 🎤, botón enviar circular →

#### 11. Configuración (`ConfiguracionFragment`)
- Avatar + botón "Modo Concentración", "Perfil", toggle "Modo Oscuro"
- Botón "Cierre de sesión" al fondo (texto rojo)

---

## 💬 DETALLE DEL CHAT CON IA (PRIORIDAD SPRINT ACTUAL)

### Qué es el Chat con IA en YOESTUD.IO

El chat es un asistente académico integrado en la app. Usa la **Gemini API** a través del servidor Spring Boot (la app NO llama a Gemini directamente). El servidor actúa como proxy inteligente.

### System prompt que el servidor debe enviar a Gemini

```
Eres un asistente académico integrado en YOESTUD.IO, una app de estudio para estudiantes universitarios.

Tu función principal es:
1. Responder preguntas académicas de forma clara y didáctica.
2. Ayudar al usuario a comprender material de estudio que él mismo suba.
3. Cuando el usuario pida generar una "prueba", "test" o "evaluación" sobre un tema, 
   DEBES responder ÚNICAMENTE con un JSON válido con esta estructura exacta:
   {
     "tipo": "archivo",
     "nombre_archivo": "prueba_[tema]_[fecha].txt",
     "contenido": "...contenido completo de la prueba en texto plano..."
   }
   El servidor detectará este JSON y lo guardará como archivo en /output/.

Comportamiento:
- Responde siempre en español.
- Sé conciso pero completo.
- Si el usuario sube una imagen o código, analízalo en contexto académico.
- No respondas preguntas que no sean académicas o de uso de la app.
- Si el usuario pide una prueba, inclúye: título, instrucciones, 5-10 preguntas variadas 
  (selección múltiple, desarrollo, verdadero/falso) con sus respuestas al final.
```

### Flujo del chat con generación de archivos

```
Usuario escribe mensaje
        ↓
App Android → POST /api/chat/mensaje
  { usuario_id, sesion_id, mensaje }
        ↓
Servidor Spring Boot:
  1. Carga historial de mensajes_chat de MongoDB
  2. Construye array de mensajes para Gemini (incluyendo system prompt)
  3. Llama a Gemini API
  4. Recibe respuesta
  5. ¿La respuesta contiene JSON con "tipo":"archivo"?
     SÍ → guarda archivo en /output/, registra en MongoDB archivos_generados
          → responde al app con { mensaje: "Prueba generada. [Descargar]", archivo_id: "..." }
     NO → responde normalmente
  6. Guarda turno en mensajes_chat MongoDB
        ↓
App muestra respuesta en el chat
(si hay archivo_id → muestra botón "Descargar prueba")
```

### Endpoints del Chat

- `POST /api/chat/mensaje` — enviar mensaje, recibe respuesta IA
- `GET /api/chat/historial/{usuario_id}` — cargar historial
- `DELETE /api/chat/sesion/{sesion_id}` — limpiar conversación
- `GET /api/archivos/{archivo_id}` — descargar archivo generado (ResponseEntity<Resource>)
- `GET /api/archivos/usuario/{usuario_id}` — listar archivos generados por el usuario

---

## 📋 PRODUCT BACKLOG

| ID | Historia | Prioridad |
|---|---|---|
| US01 | Registro de usuario | Alta |
| US02 | Inicio de sesión | Alta |
| US03 | Subir material | Alta |
| US04 | Buscar material | Alta |
| US05 | Descargar material | Alta |
| US06 | Calificar material | Media |
| US07 | Actualizar material | Media |
| US08 | Gestión admin | Media |
| US09 | Recompensas | Baja |
| US10 | Historial sesiones | Baja |
| US11 | Recomendaciones | Baja |
| **US12** | **Chat con IA (responder preguntas)** | **Alta** |
| **US13** | **Generación de archivos de prueba por IA** | **Media** |

---

## 🗂️ REGLAS DE TRABAJO

1. **Un paso por vez.** Espera confirmación antes de continuar.
2. **Código completo y funcional**, no pseudocódigo.
3. **Cada archivo**: comentario en primera línea con nombre, propósito y fecha.
4. **Para cada funcionalidad** indica: archivos a crear, archivos a modificar, commit message sugerido.
5. **Formato de commits:** `feat:`, `fix:`, `refactor:`
6. **Tests:** al menos 1 test unitario básico por endpoint.
7. **Nomenclatura:** español para negocio (usuario, material, sesion), inglés para técnico (Repository, Service, Controller).
8. **El servidor corre en localhost/IP local.** La app usa una constante `BASE_URL` configurable.

---

## 🚀 PLAN DE SPRINTS

### ✅ Sprint 0 — Ya hecho
- Tablas NeonDB creadas: `usuarios`, `roles`, `pagos` con datos de prueba.

---

### Sprint 1 — Fundación backend

#### Paso 1.1 — Estructura del proyecto Spring Boot
Estructura de paquetes:
```
com.yoestudio
├── config/        (DB, Security, CORS)
├── usuario/       (model, repository, service, controller)
├── material/      (model, repository, service, controller)
├── chat/          (model, repository, service, controller)
├── archivo/       (model, repository, service, controller)
└── seguridad/     (JWT, filtros)
```
`pom.xml` con: Spring Web, Spring Data JPA, Spring Data MongoDB, Spring Security, PostgreSQL driver, JWT (jjwt), Lombok, Validation, Commons IO (para manejo de archivos).

**Commit:** `feat: estructura base Spring Boot con dependencias`

---

#### Paso 1.2 — Entidades JPA (NeonDB)
Entidades que mapeen exactamente `usuarios`, `roles`, `pagos`. Respeta tipos y nombres de columnas. Relaciones `@ManyToOne`.

**Commit:** `feat: entidades JPA Usuario Rol Pago`

---

#### Paso 1.3 — Documentos MongoDB
Clases `@Document` para: `Material`, `MensajesChat`, `ArchivoGenerado` según los esquemas definidos arriba.

**Commit:** `feat: documentos MongoDB Material MensajesChat ArchivoGenerado`

---

#### Paso 1.4 — Configuración de bases de datos
`application.properties` con:
- `spring.datasource.url=${NEON_DB_URL}` (NeonDB)
- `spring.data.mongodb.uri=${MONGO_URI}` (MongoDB)
- Puerto 8080
- CORS habilitado para cualquier origen (desarrollo local)
- Carpeta de output para archivos generados: `app.output.dir=./output`

**Commit:** `feat: configuración NeonDB y MongoDB`

---

#### Paso 1.5 — Autenticación (US01, US02)
- `POST /api/auth/registro` — crea usuario con BCrypt, rol ESTUDIANTE por defecto
- `POST /api/auth/login` — valida y retorna JWT

Incluye: DTOs, AuthService, JwtUtil, JwtFilter, SecurityConfig que proteja todo excepto `/api/auth/**` y `GET /api/archivos/**`.

**Commit:** `feat: autenticación registro login JWT`

---

### Sprint 2 — Chat con IA (PRIORIDAD ACTUAL)

#### Paso 2.1 — Servicio Gemini en el servidor
Crea `GeminiService` que:
1. Reciba el historial de mensajes y el nuevo mensaje del usuario.
2. Construya el request a la Gemini API incluyendo el system prompt definido arriba (verbatim).
3. Use `WebClient` o `RestTemplate` para llamar a `https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent`.
4. Retorne el texto de la respuesta.
5. Detecte si la respuesta contiene JSON con `"tipo":"archivo"` y, si es así, retorne un objeto especial `GeminiRespuesta` con flag `esArchivo=true` y el contenido parseado.

API Key en variable de entorno `${GEMINI_API_KEY}`.

**Commit:** `feat: GeminiService integración API con detección de archivos`

---

#### Paso 2.2 — Servicio de archivos generados
Crea `ArchivoService` que:
1. Reciba nombre y contenido de texto.
2. Cree el archivo físico en `./output/{usuario_id}/`.
3. Registre el archivo en MongoDB (`archivos_generados`).
4. Retorne el `archivo_id`.

Endpoint `GET /api/archivos/{archivo_id}` que sirva el archivo como `ResponseEntity<Resource>` con header `Content-Disposition: attachment`.

**Commit:** `feat: ArchivoService generación y descarga de archivos`

---

#### Paso 2.3 — ChatController y ChatService
Implementa:
- `ChatService`: carga historial de MongoDB, llama a `GeminiService`, guarda turno en MongoDB, si es archivo llama a `ArchivoService`.
- `ChatController`:
  - `POST /api/chat/mensaje` (requiere JWT): cuerpo `{ sesion_id, mensaje }`, retorna `{ respuesta, es_archivo, archivo_id? }`
  - `GET /api/chat/historial/{usuario_id}` (requiere JWT)
  - `DELETE /api/chat/sesion/{sesion_id}` (requiere JWT)

**Commit:** `feat: ChatController y ChatService con historial MongoDB`

---

### Sprint 3 — App Android

#### Paso 3.1 — Estructura Android + tema visual
Crea el proyecto Android con:
- Arquitectura MVVM
- Paquetes: `ui/`, `viewmodel/`, `model/`, `repository/`, `network/`, `utils/`
- `build.gradle`: Retrofit2, Gson, Coroutines, LiveData, ViewModel, Navigation Component, Material Design 3, DataStore
- `themes.xml` con los dos temas (claro/oscuro) según la paleta definida arriba:
  - Fondo oscuro: `#0D1B2A`
  - Primary oscuro: `#0000FF`
  - Surface oscuro: `#1A2B3C`
- `colors.xml` con todos los tokens de color de la paleta

**Commit:** `feat: estructura Android MVVM + sistema de temas claro/oscuro`

---

#### Paso 3.2 — Layouts de Login y Registro
Crea los layouts XML que reproduzcan fielmente el diseño del prototipo:
- `activity_login.xml`: fondo color background, logo, ícono circular, card con campos y botones
- `activity_register.xml`: igual con campos de registro y checkbox de términos
- Ambos con soporte para modo oscuro via `?attr/colorSurface`, `?attr/colorPrimary` etc.
- `LoginActivity.kt` y `RegisterActivity.kt` con ViewModel, llamada a `/api/auth/login` y `/api/auth/registro`, guarda JWT en DataStore.

**Commit:** `feat: pantallas Login y Registro con diseño del prototipo`

---

#### Paso 3.3 — NavigationDrawer + Home
- `MainActivity.kt` con `DrawerLayout` + `NavigationView`
- Menú lateral con todos los ítems del prototipo (iconos + labels)
- Toggle modo oscuro funcional (guarda preferencia en DataStore, aplica `AppCompatDelegate.setDefaultNightMode`)
- `HomeFragment.kt`: saludo con nombre de usuario, barra de búsqueda, historial (últimos 5 materiales vistos), configuración rápida

**Commit:** `feat: NavigationDrawer Home y toggle modo oscuro`

---

#### Paso 3.4 — Pantalla Chat con IA (PRIORIDAD)
Crea `ChatIAFragment` que reproduzca fielmente el diseño:
- `RecyclerView` con dos tipos de item: mensaje usuario (derecha, globo blanco) y mensaje IA (izquierda, globo gris azulado)
- Input inferior con campo de texto + íconos (imagen, código, micrófono) + botón enviar circular
- Fondo `#0A1628`
- `ChatViewModel` que llame a `POST /api/chat/mensaje` con Retrofit, maneje estado (loading/success/error)
- Si la respuesta trae `es_archivo=true`, mostrar en el chat un mensaje especial con botón "📄 Descargar prueba generada" que llame a `GET /api/archivos/{archivo_id}` y guarde el archivo en el almacenamiento del teléfono

**Commit:** `feat: ChatIAFragment diseño y conexión con backend`

---

#### Paso 3.5 — Pantallas de material
- `MaterialListFragment`: RecyclerView de cards con imagen placeholder, título, descripción
- `MaterialDetailFragment`: detalle con botones Descargar y Calificar
- `CalificarFragment`: 5 estrellas interactivas + campo de comentario + botón Enviar
- `SubirMaterialFragment`: file picker + campo descripción + botón Subir

**Commit:** `feat: pantallas material listado detalle calificar subir`

---

#### Paso 3.6 — Documentos descargados y Configuración
- `DescargasFragment`: spinner de asignatura + lista expandible de documentos descargados localmente
- `ConfiguracionFragment`: reproducir pantalla de configuración del prototipo (Modo Concentración, Perfil, Modo Oscuro, Cerrar Sesión)

**Commit:** `feat: pantallas descargas y configuración`

---

### Sprint 4 — Generación de archivos (tarea final)

#### Paso 4.1 — Prueba end-to-end de generación
Instrucciones paso a paso para probar el flujo completo:
1. El usuario escribe en el chat: "Genera una prueba de matemáticas sobre álgebra lineal"
2. El servidor llama a Gemini con el system prompt, recibe JSON con el contenido
3. El servidor crea `./output/{usuario_id}/prueba_algebra_lineal_FECHA.txt`
4. Registra en MongoDB `archivos_generados`
5. La app muestra en el chat un botón de descarga
6. El usuario toca el botón → el archivo se descarga al teléfono

Incluye: test de integración del `ChatController`, instrucciones para verificar que el archivo se creó en el servidor, instrucciones para verificar la descarga en el teléfono.

**Commit:** `test: prueba end-to-end generación y descarga de archivos IA`

---

## 💬 CÓMO USAR ESTE PROMPT

1. **Al iniciar sesión nueva:** pega este prompt y di: _"Estamos en el Paso [X.X]. Continúa desde ahí."_
2. **Para avanzar:** di _"Paso completado, avanza al siguiente."_
3. **Si hay error:** di _"Tuve este error: [error]. Ayúdame a resolverlo antes de continuar."_
4. **Consulta puntual:** no necesitas pegar el prompt completo, solo menciona en qué paso van.

---

*YOESTUD.IO — Taller Aplicado de Programación 2026*
*Equipo: Belén Alarcón · Benjamín Belmar · Marcel Droguett · Matías Sanhueza*
