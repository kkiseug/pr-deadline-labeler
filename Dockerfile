FROM gradle:8.5-jdk21 AS build

WORKDIR /app
COPY . .
RUN gradle jar --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/pr-deadline-labeler-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
