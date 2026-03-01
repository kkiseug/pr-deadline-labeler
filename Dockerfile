FROM gradle:8.5-jdk21 AS build

WORKDIR /app
COPY . .
RUN gradle jar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/build/libs/deadline-labeler-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
