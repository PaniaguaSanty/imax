FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

# Copiar el POM padre desde la raíz del proyecto
COPY pom.xml .

# Copiar el POM y código del notification-service
COPY imax-notification-service/pom.xml ./imax-notification-service/
COPY imax-notification-service/mvnw ./imax-notification-service/
COPY imax-notification-service/.mvn ./imax-notification-service/.mvn

# Cambiar al directorio del servicio
WORKDIR /workspace/imax-notification-service

# Descargar dependencias
RUN ./mvnw dependency:go-offline -DskipTests

# Copiar código fuente
COPY imax-notification-service/src ./src

# Compilar aplicación
RUN ./mvnw clean package -DskipTests

# Etapa final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar JAR desde etapa de build
COPY --from=build /workspace/imax-notification-service/target/*.jar app.jar

# Exponer puerto
EXPOSE 8084

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]