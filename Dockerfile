# Используем базовый образ Java 17 на Alpine Linux
FROM eclipse-temurin:17-jre-alpine AS base

# Устанавливаем переменную среды JAVA_OPTS для оптимизации производительности
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Копируем готовый JAR-файл в рабочую директорию внутри контейнера
COPY target/jira-1.0.jar app.jar

# Экспонируем порт, на котором работает ваше приложение (обычно 8080)
EXPOSE 8080

# Команда запуска приложения
CMD ["java", "-jar", "/app.jar"]