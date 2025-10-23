# ---------- build stage ----------
FROM gradle:8.6.0-jdk17-alpine AS build
WORKDIR /workspace

# copy the entire server module into the build stage
COPY . .

RUN gradle bootJar --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar

# Persist SQLite file outside the container
VOLUME /app/data
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/mySpendingApp.db

# Persist application logs outside the container
VOLUME /app/logs

# Health-check: hit Spring Boot Actuator every 30 s
HEALTHCHECK --interval=30s --timeout=5s --retries=3 CMD \
  wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
