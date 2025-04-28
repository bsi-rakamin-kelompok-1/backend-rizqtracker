FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY build/libs/*.jar app.jar

# The host directory ./uploads/images must be chmod 777
RUN mkdir -p /app/uploads/images && chmod 777 /app/uploads/images

RUN useradd -m appuser && chown -R appuser:appuser /app

VOLUME /app/uploads/images

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]