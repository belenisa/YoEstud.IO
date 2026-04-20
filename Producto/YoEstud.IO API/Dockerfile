# Etapa 1: Construcción (Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos solo el pom.xml primero para aprovechar el cache de Docker
COPY pom.xml .
COPY src ./src
# Compilamos saltando los tests
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Solo el JRE de Java 21 para que sea más ligero)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Comando para arrancar la API
ENTRYPOINT ["java", "-jar", "app.jar"]