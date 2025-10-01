# Usamos la imagen oficial de OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio dentro del contenedor
WORKDIR /app

# Copiamos el jar construido con Maven
COPY target/imax-showTime-service-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto que usa showtime-service
EXPOSE 8082

# Comando para arrancar la app
ENTRYPOINT ["java", "-jar", "app.jar"]