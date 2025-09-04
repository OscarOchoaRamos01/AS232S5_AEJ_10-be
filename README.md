# Servicios de IA con Spring Boot

Este proyecto integra servicios de IA para generación de conversaciones inteligentes y creación de imágenes, desarrollado con Spring Boot y tecnologías reactivas.

## ✅ APIs de IA Integradas

### 1. ChatGPT API

- **Funcionalidad**: Generación de conversaciones y respuestas inteligentes
- **Características**:
  - Procesamiento de lenguaje natural
  - Acceso web habilitado
  - Conversaciones contextuales

### 2. Image Generator API

- **Funcionalidad**: Generación de imágenes mediante IA
- **Características**:
  - Generación basada en prompts de texto
  - Múltiples estilos y tamaños disponibles
  - Integración reactiva con WebFlux

## ✅ Herramientas y Tecnologías Utilizadas

### Lenguaje y Entorno

- **Java**: JDK 17
- **Framework**: Spring Boot 3.4.4
- **Programación Reactiva**: Spring WebFlux y Project Reactor

### Base de Datos

- **PostgreSQL**: Con soporte R2DBC para programación reactiva
- **MongoDB**: Alternativa NoSQL con Spring Data MongoDB Reactive

### Gestión de Dependencias

- **Maven**: Apache Maven para gestión del proyecto
- **Lombok**: Reducción de código boilerplate

### APIs y Servicios Externos

- **ChatGPT API**: Para funcionalidades de chat inteligente
- **Image Generator API**: Para generación de imágenes con IA

### Documentación

- **Swagger/OpenAPI**: Con SpringDoc para documentación automática de APIs

### IDEs Compatibles

- IntelliJ IDEA
- Visual Studio Code
- GitHub Codespaces

## **Dependencias Spring WebFlux + Postgre (SQL)**

Spring WebFlux | Data R2DBC | Project Reactor | R2DBC PostgreSQL

```
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
      <groupId>io.projectreactor</groupId>
      <artifactId>reactor-test</artifactId>
      <scope>test</scope>
</dependency>
<dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>r2dbc-postgresql</artifactId>
      <scope>runtime</scope>
</dependency>
```

## **Dependencias Spring WebFlux + MongoDB (NoSQL)**

Spring WebFlux | Data MongoDB Reactive | Project Reactor

```
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
<dependency>
      <groupId>io.projectreactor</groupId>
      <artifactId>reactor-test</artifactId>
      <scope>test</scope>
</dependency>
```

## **Dependencias Swagger para Spring WebFlux**

```
<dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
      <version>2.0.2</version>
</dependency>
```
