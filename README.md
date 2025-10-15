# 🌤️ Prueba Desarrollador - Previsión AEMET

Aplicación completa (Angular + Spring Boot) que consume la API de la **AEMET** para mostrar previsiones meteorológicas de municipios españoles.

---

## 🧩 Estructura del proyecto
```
📂 prevision-aemet-v10/
├── backend/              # Proyecto Spring Boot (API REST)
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│
├── frontend/             # Proyecto Angular (interfaz de usuario)
│   ├── src/
│   ├── package.json
│   ├── Dockerfile
│
└── README.md
```
---

## ⚙️ Ejecución en local

### 1️⃣ Backend
cd backend
mvn spring-boot:run
# Servirá la API en: http://localhost:8080

### 2️⃣ Frontend
cd frontend
npm install
ng serve
# Servirá la web en: http://localhost:4200

---


## 🧰 Configuración necesaria

El backend requiere una **API Key válida de AEMET**, configurada en:
backend/src/main/resources/application.properties

aemet.base-url=https://opendata.aemet.es/opendata/api
aemet.api-key=API-KEY

---

## 📡 Endpoints principales

Método | Endpoint | Descripción
-------|-----------|--------------
GET | /api/municipalities?name={nombre} | Busca municipios por nombre
GET | /api/forecast/{idMunicipio} | Obtiene la previsión meteorológica del municipio

---

## 🐋 Dockerfiles incluidos

### backend/Dockerfile

# Etapa 1: Compilación del backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/weather-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

---

### frontend/Dockerfile

# Etapa 1: Construcción del frontend Angular
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production

# Etapa 2: Servidor NGINX para servir la app
FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

---

## 🧱 Imágenes Docker publicadas en GHCR

Como parte del despliegue final, las imágenes Docker del **backend** y **frontend** han sido construidas y publicadas en el **GitHub Container Registry (GHCR)**, cumpliendo con el requisito de entrega.

### 📦 Imágenes disponibles:

Componente | Imagen | Estado
-------------|---------|--------
Backend (Spring Boot) | ghcr.io/miguel-moncayo/prevision-aemet-backend:latest | ✅ Publicada
Frontend (Angular) | ghcr.io/miguel-moncayo/prevision-aemet-frontend:latest | ✅ Publicada

Estas imágenes están **disponibles públicamente** y pueden ejecutarse sin necesidad de credenciales.

---

### ▶️ Ejecución con Docker

**Backend**
docker pull ghcr.io/miguel-moncayo/prevision-aemet-backend:latest
docker run -p 8080:8080 ghcr.io/miguel-moncayo/prevision-aemet-backend:latest

**Frontend**
docker pull ghcr.io/miguel-moncayo/prevision-aemet-frontend:latest
docker run -p 4200:80 ghcr.io/miguel-moncayo/prevision-aemet-frontend:latest

Una vez levantados:
- Frontend → http://localhost:4200
- Backend → http://localhost:8080/api


---

## 💡 Tecnologías usadas

- Backend: Spring Boot 3, WebFlux, Maven, Java 17
- Frontend: Angular 17, TypeScript, HTML, CSS
- Infraestructura: Docker, NGINX, Eclipse Temurin JDK 17
---

## ✅ Estado del proyecto

Módulo | Estado
--------|--------
Backend | ✅ 100% funcional
Frontend | ✅ 100% funcional
Integración | ✅ Probada y correcta
Docker & README | ✅ Incluidos

---

Autor: Miguel Moncayo
Fecha: Octubre 2025

