Sistema de gestión de cine desarrollado con una arquitectura de microservicios en Java Spring Boot. Microservicios | Kafka | Docker | asincronía | eureka.

<img width="1561" height="676" alt="image" src="https://github.com/user-attachments/assets/9fe04c2d-f14f-4521-9ecc-11be56dcb6d8" />


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
      git clone https://github.com/Paniaguasanty/imax.git
   cd imax


# 🤝 Contribuciones
Las contribuciones son bienvenidas. Por favor:

Haz un fork del proyecto

Crea una rama para tu feature (git checkout -b feature/AmazingFeature)

Commit tus cambios (git commit -m 'Add some AmazingFeature')

Push a la rama (git push origin feature/AmazingFeature)

Abre un Pull Request

# 📊 Estado del Proyecto
# ✅ Completado
Definición de arquitectura

Configuración inicial de microservicios

# 🚧 En Progreso
Servicio de Catálogo

Servicio de Reservas

Servicio de Notificaciones

# 📋 Pendiente
API Gateway

Service Discovery

Frontend integrado

Pipeline CI/CD

Despliegue en cloud

# 📈 Próximas Mejoras
Integración con pasarelas de pago reales

Sistema de fidelización y puntos

App móvil nativa

Sistema de recomendaciones avanzado con machine learning

Análisis de datos en tiempo real de ocupación

# 📞 Contacto
Paniagua Santiago - paniaguasanty10@gmail.com

LinkedIn en mi perfil.

# 🙏 Agradecimientos
Comunidad de Java por las librerías y herramientas

Plataformas de despliegue gratuitas por hacer posible mostrar el proyecto

# ⭐️ ¡Si te gusta este proyecto, no olvides darle una estrella!
