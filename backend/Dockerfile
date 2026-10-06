# ===== Etapa 1: build con Maven =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos solo el pom primero para cachear la capa de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora el código fuente y empaquetado (tests se corren fuera del build de imagen)
COPY src ./src
RUN mvn package -DskipTests -B

# ===== Etapa 2: runtime liviano =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/banco-api-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
