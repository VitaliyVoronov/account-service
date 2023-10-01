FROM gradle:jdk17-alpine as builder

WORKDIR /app

COPY build.gradle /app
COPY settings.gradle /app
COPY src /app/src

RUN gradle clean build


FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/account-service.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]