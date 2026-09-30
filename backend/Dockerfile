# Build stage: compile and package the Spring Boot jar.
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# Runtime stage: JRE only, running as an unprivileged user.
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S -G app app
WORKDIR /app
COPY --from=build /build/target/backend-*.jar app.jar
USER app
ENV LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
