FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /builder
ARG APP_JAR=build/libs/*.jar
COPY ${APP_JAR} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="marakogr@mail.ru"
LABEL description="Дружбометр - аналитика Instagram чатов"
WORKDIR /application
COPY --from=extractor /builder/dependencies/ ./
COPY --from=extractor /builder/spring-boot-loader/ ./
COPY --from=extractor /builder/snapshot-dependencies/ ./
COPY --from=extractor /builder/application/ ./
EXPOSE 8080
EXPOSE 5005
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]