FROM eclipse-temurin:17

#RUN addgroup spring
#RUN useradd -g spring spring

#USER spring

WORKDIR /opt/spring-app

COPY . .
RUN ./gradlew clean build -x test

CMD [ "java", "-jar", "build/libs/whooa-blog-0.0.1-SNAPSHOT.jar" ]