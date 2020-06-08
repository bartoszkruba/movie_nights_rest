FROM openjdk:11

VOLUME /tmp

ADD maven/movie_nights_rest-${project.version}.jar movie_nights_rest.jar

RUN sh -c 'touch /movie_nights_rest.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/movie_nights_rest.jar"]