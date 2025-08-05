FROM openjdk:17
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar", "app.jar"]