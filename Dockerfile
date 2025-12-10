# Multi-stage build: Build stage
FROM maven:3.8.4-openjdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the WAR file
RUN mvn clean package -DskipTests

# Runtime stage
FROM tomcat:7.0.47-jre8

# Copy the built WAR file from build stage to Tomcat webapps directory as ROOT.war
COPY --from=build /app/target/CourseManagementSystem.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]