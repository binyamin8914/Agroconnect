# ---------- Etapa 1: compilación (imagen con JDK y Gradle) ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q > /dev/null
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ---------- Etapa 2: ejecución (imagen liviana solo con JRE) ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/servicio-pedidos.jar app.jar
EXPOSE 8080
USER 1001
ENTRYPOINT ["java", "-jar", "app.jar"]
