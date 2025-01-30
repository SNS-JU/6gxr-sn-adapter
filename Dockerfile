FROM openjdk:22-jdk-slim
WORKDIR /sn-adapter
COPY build/libs/sn-adapter-3.1.0.jar sn-adapter.jar
EXPOSE 8080
CMD ["java", "-jar", "sn-adapter.jar"]