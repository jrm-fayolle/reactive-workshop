call gradlew :backend:bootJar
call gradlew :external-service:bootJar

call docker build -t backend-img .

call docker compose up