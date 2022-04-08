FROM openjdk:17.0.1-slim as builder
WORKDIR taggit
ARG JAR_FILE=backend/build/libs/*.jar
COPY ${JAR_FILE} taggit.jar
RUN java -Djarmode=layertools -jar taggit.jar extract

FROM openjdk:17.0.1-slim
WORKDIR taggit
COPY --from=builder taggit/dependencies/ ./
COPY --from=builder taggit/spring-boot-loader/ ./
COPY --from=builder taggit/snapshot-dependencies/ ./
RUN true
COPY --from=builder taggit/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]