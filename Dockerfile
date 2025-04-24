FROM eclipse-temurin:17-jre-alpine AS base

WORKDIR /app

COPY target/jira-1.0.jar jira.jar

COPY resources /app/resources

EXPOSE 8080

CMD ["java", "-jar", "/app/jira.jar"]
