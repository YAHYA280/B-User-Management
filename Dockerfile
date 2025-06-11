FROM openjdk:17-jdk-slim


ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 7071

ENV SPRING_PROFILES_ACTIVE=local


ENTRYPOINT ["java", "-jar", "/app.jar"]
