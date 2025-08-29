# Multi-stage Docker build for production-ready Spring Boot AI MongoDB MCP Server
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (for better layer caching)
RUN ./mvnw dependency:resolve dependency:resolve-sources

# Copy source code
COPY src/ src/

# Build the application
RUN ./mvnw clean package -DskipTests && \
    java -Djarmode=layertools -jar target/*.jar extract

# Production stage
FROM eclipse-temurin:21-jre-alpine AS production

# Install required packages
RUN apk add --no-cache \
    curl \
    ca-certificates \
    tzdata && \
    addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001 -G spring

# Set timezone
ENV TZ=UTC

# Create app directory
WORKDIR /app

# Copy built application layers
COPY --from=builder --chown=spring:spring /app/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/application/ ./

# Switch to non-root user
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set JVM options for production
ENV JAVA_OPTS="-XX:+UseZGC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -XX:+HeapDumpOnOutOfMemoryError \
               -XX:HeapDumpPath=/tmp/heapdump.hprof \
               -Djava.security.egd=file:/dev/./urandom"

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

# Labels for metadata
LABEL maintainer="officeWorkPlace <office.place.work.007@gmail.com>"
LABEL version="1.0.0"
LABEL description="Production-ready Spring Boot AI MongoDB MCP Server"
LABEL org.opencontainers.image.source="https://github.com/officeWorkPlace/spring-boot-ai-mongodb-mcp-server"
LABEL org.opencontainers.image.title="Spring Boot AI MongoDB MCP Server"
LABEL org.opencontainers.image.description="Production-ready Model Context Protocol server for MongoDB with AI integration"
LABEL org.opencontainers.image.vendor="officeWorkPlace"
LABEL org.opencontainers.image.licenses="MIT"