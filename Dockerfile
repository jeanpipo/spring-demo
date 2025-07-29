FROM amazoncorretto:21

COPY target/spring-sample-0.0.1-SNAPSHOT.jar /spring-sample.jar
COPY run.sh /run.sh

RUN chmod 755 run.sh

CMD ["./run.sh"]