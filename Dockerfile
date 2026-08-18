FROM eclipse-temurin:26-jre
WORKDIR /app

# copier le jar dans la racine /app.jar (ENTRYPOINT reste /app.jar)
COPY target/*.jar /app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]