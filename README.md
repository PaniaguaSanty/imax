# 🎬 Sistema de Gestión de Cine - IMAX

Este proyecto es un sistema de gestión de cine desarrollado con una arquitectura de microservicios en Java Spring Boot. Permite a los usuarios explorar la cartelera, reservar entradas y recibir notificaciones por correo electrónico con sus boletos en formato QR.

## 🚀 Características

- **Microservicios**: Arquitectura escalable y desacoplada.
- **Gestión de Contenido**: CRUD de películas, salas, horarios y promociones.
- **Sistema de Reservas**: Proceso de reserva con generación de QR.
- **Notificaciones**: Envío de correos electrónicos con tickets.
- **Motor de Recomendaciones**: Sugerencias personalizadas basadas en preferencias.
- **Panel Administrativo**: Dashboard para gestión y reportes.

## 🏗️ Arquitectura

El sistema está compuesto por los siguientes microservicios:

1. **Servicio de Contenido**: Gestiona películas, salas, horarios y promociones.
2. **Servicio de Reservas**: Maneja las reservas y la generación de códigos de reserva.
3. **Servicio de Notificaciones**: Se encarga del envío de emails y generación de QR.

### Comunicación entre servicios

- **Síncrona**: REST APIs para operaciones inmediatas.
- **Asíncrona**: Kafka para eventos de reservas y notificaciones.

## 🛠️ Tecnologías Utilizadas

- **Java 17**: Lenguaje de programación principal.
- **Spring Boot 3**: Framework para desarrollar los microservicios.
- **Spring Cloud**: Para patrones de microservicios (Gateway, Service Discovery).
- **Spring Data JPA**: Persistencia de datos.
- **PostgreSQL/MySQL**: Bases de datos relacionales.
- **Kafka**: Mensajería asíncrona.
- **Docker**: Containerización de servicios.
- **Docker Compose**: Orquestación de contenedores.
- **Jenkins**: Pipeline de CI/CD.
- **Thymeleaf**: Motor de plantillas para el frontend.
- **Bootstrap 5**: Framework CSS para el diseño responsive.

## 📦 Instalación y Despliegue

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose
- Una instancia de Kafka (puede ser local con Docker)

### Pasos para ejecutar localmente

1. Clona el repositorio:
   ```bash
      git clone https://github.com/tu-usuario/imax.git
   cd imax
