# Usamos imagen de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Creamos directorio de la app dentro del contenedor
WORKDIR /app

# Copiamos el jar generado desde target
COPY target/imax-api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto que usa el gateway
EXPOSE 8090

# Comando para levantar la app
ENTRYPOINT ["java","-jar","app.jar"]