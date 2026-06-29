FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY backend/ backend/
COPY shared/ shared/
WORKDIR /workspace/backend
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
