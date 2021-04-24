FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD backend/build/libs/backend-0.0.1-SNAPSHOT.jar .
ADD external-service/build/libs/external-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["ls","-l","*.jar"]
