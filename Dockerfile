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
COPY library-application ./library-application

# Build the project
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install -DskipTests -T 1C
