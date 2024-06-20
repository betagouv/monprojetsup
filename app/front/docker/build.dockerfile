FROM node:20-alpine3.20 AS build

WORKDIR /app

COPY ../. .

RUN npm ci

RUN npm run build

FROM nginx:stable-alpine

COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /var/www/html/

EXPOSE 3001

ENTRYPOINT ["nginx","-g","daemon off;"]