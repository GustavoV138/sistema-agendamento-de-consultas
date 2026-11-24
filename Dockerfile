# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy Maven wrapper and dependencies configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy project source and package
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Create non-root user and group
RUN groupadd -r spring && useradd -r -g spring spring

# Copy jar artifact from build stage
COPY --from=build /app/target/*.jar app.jar
RUN chown -R spring:spring /app

USER spring:spring

ENV SPRING_PROFILES_ACTIVE=demo
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xss512k"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
