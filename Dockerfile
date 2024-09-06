# BellSoft에서 OpenJDK Java LTS 17을 다운받는다.
FROM alpine:3.16 AS base
ENV JAVA_HOME /opt/jdk/jdk-17.0.12
ENV PATH $JAVA_HOME/bin:$PATH

# Alpine Linux로 설정된 릴리즈를 다운받는다.
ADD https://download.bell-sw.com/java/17.0.12+10/bellsoft-jdk17.0.12+10-linux-x64-musl.tar.gz /opt/jdk/
RUN tar -xzvf /opt/jdk/bellsoft-jdk17.0.12+10-linux-x64-musl.tar.gz -C /opt/jdk/

WORKDIR /opt/spring_app
COPY . .
RUN ./gradlew clean build -x test

# jlink 명령으로 맞춤 JDK 이미지를 생성한다(jlink를 사용해서 애플리케이션에 필요한 모듈만 선택하여 실행 이미지로 링크할 수 있다.).
RUN ["jlink", "--compress=2", \
     "--module-path", "/opt/jdk/jdk-17.0.12/jmods/", \
     "--add-modules", "java.base,java.sql,java.xml,java.management,java.compiler,java.instrument,java.scripting,java.logging,java.naming,java.desktop,jdk.unsupported", \
     "--no-header-files", "--no-man-pages", \
     "--output", "/springboot-runtime"]

FROM alpine:3.16
COPY --from=base /springboot-runtime /opt/jdk
ENV PATH=$PATH:/opt/jdk/bin
EXPOSE 3000
COPY --from=base /opt/spring_app/build/libs/whooa-blog-0.0.1-SNAPSHOT.jar /opt/spring_app/
CMD ["java", "-jar", "/opt/spring_app/whooa-blog-0.0.1-SNAPSHOT.jar"]