FROM alpine:3.16 AS base

ENV JAVA_HOME /opt/jdk/jdk-17.0.12+7
ENV PATH $JAVA_HOME/bin:$PATH

ADD https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12%2B7/OpenJDK17U-jdk_x64_alpine-linux_hotspot_17.0.12_7.tar.gz /opt/jdk/
RUN tar -xzvf /opt/jdk/OpenJDK17U-jdk_x64_alpine-linux_hotspot_17.0.12_7.tar.gz -C /opt/jdk/


FROM base AS source

WORKDIR /opt/app
COPY . .

RUN ./gradlew clean build -x test
RUN ["jlink", "--compress=2", \
     "--module-path", "/opt/jdk/jdk-17.0.12+7/jmods/", \
     "--add-modules", "java.base,java.sql,java.xml,java.management,java.compiler,java.security.jgss,java.instrument,java.scripting,java.logging,java.naming,java.desktop,jdk.unsupported", \
     "--no-header-files", "--no-man-pages", \
     "--output", "/runtime"]


FROM alpine:3.16 AS prod

ARG USER=spring
RUN adduser --no-create-home -u 1000 -D $USER

WORKDIR /opt/app
RUN chown -R 1000:1000 /opt/app

USER 1000

COPY --from=source --chown=1000:1000 /runtime /opt/jdk
COPY --from=ghcr.io/ufoscout/docker-compose-wait:latest /wait /wait

ENV PATH=$PATH:/opt/jdk/bin
EXPOSE 3000

COPY --from=source --chown=1000:1000 /opt/app/build/libs/whooa-blog-0.0.1-SNAPSHOT.jar /opt/app/

CMD ["java", "-jar", "/opt/app/whooa-blog-0.0.1-SNAPSHOT.jar"]