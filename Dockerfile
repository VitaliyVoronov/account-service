FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY . /app

CMD ["java", "-jar", "build/libs/account-service-0.0.1-SNAPSHOT-plain.jar"]