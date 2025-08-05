FROM openjdk:17
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.config.location=file:/be/conf/","-jar","app.jar"]