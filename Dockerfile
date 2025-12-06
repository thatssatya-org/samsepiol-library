FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .

COPY http/pom.xml ./http/
COPY library-core/pom.xml ./library-core/
COPY mongo/pom.xml ./mongo/
COPY temporal/pom.xml ./temporal/
COPY cache/pom.xml ./cache/
COPY cache/cache-core/pom.xml ./cache/cache-core/
COPY cache/guava/pom.xml ./cache/guava/
COPY cache/redis/pom.xml ./cache/redis/
COPY lock/pom.xml ./lock/
COPY message-queue/message-queue-core/pom.xml ./message-queue/message-queue-core/
COPY message-queue/kafka/pom.xml ./message-queue/kafka/
COPY message-queue/pom.xml ./message-queue/
COPY ai/pom.xml / ./ai/
COPY library-application/pom.xml ./library-application/

# Download dependencies
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -T 1C

# Copy the rest of the source code
COPY http ./http
COPY library-core ./library-core
COPY mongo ./mongo
COPY temporal ./temporal
COPY cache ./cache
COPY cache/cache-core ./cache/cache-core
COPY cache/guava ./cache/guava
COPY cache/redis ./cache/redis
COPY lock ./lock
COPY message-queue/message-queue-core ./message-queue/message-queue-core/
COPY message-queue/kafka ./message-queue/kafka/
COPY message-queue ./message-queue/
COPY ai / ./ai/
COPY library-application ./library-application

# Build the project
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install -DskipTests -T 1C
