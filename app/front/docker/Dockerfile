# Use an official node image as the base image
FROM node:22-alpine AS build

# Arguments declaration
ARG VITE_APP_URL
ARG VITE_API_URL
ARG VITE_PUBLIC_WEBSITE_URL
ARG VITE_KEYCLOAK_ROYAUME_URL
ARG VITE_KEYCLOAK_CLIENT_ID
ARG VITE_KEYCLOAK_CLIENT_SECRET
ARG VITE_TEST_MODE
ARG VITE_FF_MOYENNE_GENERALE
ARG VITE_PARCOURSUP_OAUTH2_URL
ARG VITE_PARCOURSUP_OAUTH2_CLIENT
ARG VITE_SENTRY_DSN
ARG VITE_SENTRY_ORG
ARG VITE_SENTRY_PROJET
ARG VITE_LAISSER_AVIS_URL
ARG VITE_ENV

# Set the working directory in the container
WORKDIR /app

# Copy frontend application files to the container
COPY . .

# Clean install a project
RUN npm ci

# Build application
RUN npm run build

# Use an official nginx image
FROM nginx:stable-alpine

# Copy node build result files to the container
COPY --from=build /app/dist /var/www/mps-front

# Nginx configuration
ENV NGINX_HOSTNAME "_"
ENV NGINX_PORT "3001"
ENV NGINX_ROOT "/var/www/mps-front"

# Copy nginx configuration
COPY ./docker/nginx.conf.template /etc/nginx/templates/nginx.conf.template

# Exposed port
EXPOSE ${NGINX_PORT}

# Command to be executed when running a container
CMD ["nginx", "-g", "daemon off;"]
