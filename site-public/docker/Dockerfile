# Use an official nginx image
FROM nginx:stable-alpine3.19

# Copy frontend application files to the container
COPY ./statics /var/www/mps-site-public/

# Nginx configuration
ENV NGINX_HOSTNAME "_"
ENV NGINX_PORT "8080"
ENV NGINX_ROOT "/var/www/mps-site-public"

# Copy nginx configuration
COPY ./docker/nginx.conf.template /etc/nginx/templates/nginx.conf.template

# Exposed port
EXPOSE ${NGINX_PORT}

# Command to be executed when running a container
CMD ["nginx", "-g", "daemon off;"]
