FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the root pom.xml
COPY pom.xml .

# Copy the pom.xml for each module
COPY http/pom.xml ./http/
COPY library-core/pom.xml ./library-core/
COPY library-application/pom.xml ./library-application/
COPY temporal/pom.xml ./temporal/

# Download dependencies
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -T 1C

# Copy the rest of the source code
COPY http ./http
COPY library-core ./library-core
COPY library-application ./library-application
COPY temporal ./temporal

# Build the project
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install -DskipTests -T 1C
