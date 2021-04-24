call gradlew :backend:bootJar
call gradlew :external-service:bootJar

call docker-compose stop
call docker build -f docker/db.Dockerfile -t postgres-db .
call docker build -f docker/openjdk.Dockerfile -t backend-img .
call docker-compose up -d