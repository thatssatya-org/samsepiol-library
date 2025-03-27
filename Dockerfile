FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY http ./http
COPY library-core ./library-core
COPY library-application ./library-application
COPY temporal ./temporal

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install -DskipTests -T 1C