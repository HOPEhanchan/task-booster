FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/task-booster-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
