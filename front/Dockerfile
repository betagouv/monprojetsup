# Use the official Nginx image as the base image
FROM nginx:latest

# Copy your static front-end files to the Nginx document root
COPY ./dist /usr/share/nginx/html

# Expose port 80 to the outside world (default for HTTP)
EXPOSE 80
