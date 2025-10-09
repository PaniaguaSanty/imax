FROM eclipse-temurin:17-jdk-alpine

# Instalar curl para que el healthcheck de Docker Compose funcione
RUN apk update && apk add curl

WORKDIR /app
COPY target/imax-eureka-config-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]