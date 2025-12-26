FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .

COPY http/pom.xml ./http/
COPY library-core/pom.xml ./library-core/
COPY repository/pom.xml ./repository/
COPY repository/repository-models/pom.xml ./repository/repository-models/
COPY repository/mysql/pom.xml ./repository/mysql/
COPY repository/mongo/pom.xml ./repository/mongo/
COPY temporal/pom.xml ./temporal/
COPY cache/pom.xml ./cache/
COPY cache/cache-core/pom.xml ./cache/cache-core/
COPY cache/guava/pom.xml ./cache/guava/
COPY cache/redis/pom.xml ./cache/redis/
COPY lock/pom.xml ./lock/
COPY message-queue/message-queue-core/pom.xml ./message-queue/message-queue-core/
COPY message-queue/kafka/pom.xml ./message-queue/kafka/
COPY message-queue/pom.xml ./message-queue/
COPY ai/pom.xml ./ai/
COPY health/pom.xml ./health/
COPY library-application/pom.xml ./library-application/

# Download dependencies
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -T 1C

# Copy the rest of the source code
COPY http ./http
COPY library-core ./library-core
COPY repository/repository-models ./repository/repository-models/
COPY repository/mysql ./repository/mysql/
COPY repository/mongo ./repository/mongo/
COPY repository ./repository/
COPY temporal ./temporal
COPY cache ./cache
COPY cache/cache-core ./cache/cache-core
COPY cache/guava ./cache/guava
COPY cache/redis ./cache/redis
COPY lock ./lock
COPY message-queue/message-queue-core ./message-queue/message-queue-core/
COPY message-queue/kafka ./message-queue/kafka/
COPY message-queue ./message-queue/
COPY ai ./ai/
COPY health ./health/
COPY library-application ./library-application
COPY lombok.config .

# Build the project
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install -DskipTests -T 1C
