FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu
#ARG JAR_FILE=ezwel-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=build/libs/KISTrading-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} KISTrading.jar
ENTRYPOINT [ "java", "-jar", "/rmo-openapi.jar" ]