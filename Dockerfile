FROM eclipse-temurin:17-jre-alpine AS base

COPY target/jira-1.0.jar app.jar

COPY resources /resources

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]