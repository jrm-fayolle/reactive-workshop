FROM postgres:13.1
ENV POSTGRES_USER docker
ENV POSTGRES_PASSWORD docker
ENV POSTGRES_DB docker
COPY docker/db/create.sql /docker-entrypoint-initdb.d/