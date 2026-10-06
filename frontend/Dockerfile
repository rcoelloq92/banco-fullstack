# ===== Etapa 1: build de Angular =====
FROM node:18-alpine AS build
WORKDIR /app

# Dependencias primero para aprovechar caché de capas
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# ===== Etapa 2: nginx sirviendo el build =====
FROM nginx:alpine

# Configuración con fallback SPA y proxy hacia el backend
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
