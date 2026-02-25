# ---- Stage 1: Build (compilation happens inside the container) ----
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /src

# Download PostgreSQL JDBC driver
RUN wget -q -O postgresql.jar \
    https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

# Copy source code and compile inside the container
COPY InsertApp.java .
RUN javac InsertApp.java

# ---- Stage 2: Run (minimal image) ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /src/InsertApp.class .
COPY --from=builder /src/postgresql.jar .

CMD ["java", "-cp", ".:/app/postgresql.jar", "InsertApp"]
