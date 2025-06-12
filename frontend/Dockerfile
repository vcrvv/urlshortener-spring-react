# Build do frontend
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Servir com nginx
FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80 