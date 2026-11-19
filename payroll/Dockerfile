
# Java 21 build environment
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give permission to execute Maven wrapper
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy application source
COPY src/ src/

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests

# Runtime environment
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy executable JAR
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
