FROM eclipse-temurin:17

# RUN addgroup spring
# RUN useradd -g spring spring

# USER spring

WORKDIR /opt/spring-app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

ENTRYPOINT [ "java", "-jar", "build/libs/*-0.0.1-SNAPSHOT.jar" ]