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

# Copiar configuración personalizada de NGINX 
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
