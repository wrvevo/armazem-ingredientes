FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl \
    && mkdir -p /data /logs
COPY --from=build /app/target/armazem-ingredientes.jar app.jar
ENV LOG_PATH=/logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
