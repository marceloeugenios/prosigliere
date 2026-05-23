FROM amazoncorretto:25-alpine

WORKDIR /app

COPY build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8080 45902

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
