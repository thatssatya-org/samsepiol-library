FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .

COPY http/pom.xml ./http/
COPY library-core/pom.xml ./library-core/
COPY mongo/pom.xml ./mongo/
COPY temporal/pom.xml ./temporal/
COPY library-application/pom.xml ./library-application/

# Download dependencies
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -T 1C

# Copy the rest of the source code
COPY http ./http
COPY library-core ./library-core
COPY mongo ./mongo
COPY temporal ./temporal
COPY library-application ./library-application

# Build the project
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install -DskipTests -T 1C
