# =========================
# Etapa 1: Build con Gradle
# =========================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos todo el proyecto
COPY . .

# CORRECCIÓN AQUÍ: Otorgar permisos de ejecución al wrapper
RUN chmod +x gradlew

# Usamos el Gradle Wrapper del proyecto
RUN ./gradlew build -x test

# =========================
# Etapa 2: Runtime
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR generado por Gradle
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
