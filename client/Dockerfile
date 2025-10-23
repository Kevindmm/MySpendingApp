# ---------- build stage ----------
FROM node:18-alpine AS build
WORKDIR /app

# Copy dependency manifests and install exact versions
COPY package*.json ./
RUN npm ci

# Copy the rest of the source code
COPY . .
RUN npm run build            # outputs static files to /app/build

# ---------- runtime stage ----------
FROM nginx:1.25-alpine

# React build
COPY --from=build /app/build /usr/share/nginx/html

# Custom Nginx config: serve React & proxy /api to backend
RUN printf 'server {\n\
  listen 80;\n\
  root /usr/share/nginx/html;\n\
  location /api/ {\n\
    proxy_pass http://backend:8080;\n\
  }\n\
  location / {\n\
    try_files $uri /index.html;\n\
  }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 80