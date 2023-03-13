FROM openjdk:8
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} news-search-app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/news-search-app.jar"]