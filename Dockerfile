# ===== STAGE 1: BUILD =====
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /poc-data-pipeline

# Copio TUTTO il progetto (che è nella root)
COPY . .

# Rendo eseguibile il gradle wrapper
RUN chmod +x ./gradlew

# Compilo il progetto
RUN ./gradlew clean build -x test

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:21-jre

WORKDIR /poc-data-pipeline

# Copio il jar generato
COPY --from=builder /poc-data-pipeline/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "input.csv", "output.csv"]

