# Use an official node image as the base image
FROM node:20-alpine3.20 AS build

# Set the working directory in the container
WORKDIR /app

# Copy frontend application files to the container
COPY . .

# Clean install a project
RUN npm ci

# Build application
RUN npm run build

# Use an official nginx image
FROM nginx:stable-alpine3.19

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
