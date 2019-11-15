#
# Download stage
#

FROM alpine/git as git
WORKDIR /app
RUN git clone https://github.com/breno500as/sca-api-gateway.git

#
# Build stage
#
FROM maven:3.5-jdk-8-alpine as builder
RUN mkdir -p /build
COPY --from=git /app/sca-api-gateway /build
WORKDIR /build
RUN mvn clean dependency:resolve dependency:resolve-plugins package spring-boot:repackage -DskipTests 
 

#
# Package stage
#
FROM openjdk:8-jdk-alpine as runtime
EXPOSE 8080
ENV APP_HOME /app
ENV JAVA_OPTS=""
RUN mkdir $APP_HOME
COPY --from=builder /build/target/*.jar app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]