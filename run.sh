#!/usr/bin/env bash

# this is NOT the place for this password, secrets !! 
# it was added here just to demostrate we can parameterize the application.properties file
export JWT_SECRET='5lYzzrQnHpyETjFQaZiWkcvfdVOhfmO05lYzzrQnHpyETjFQaZiWkcvfdVOhfmO0jjd9d9yu8m2'
export DB_USER='postgres'
export DB_PASSWORD='postgres'
export DB_HOST='jdbc:postgresql://host.docker.internal:5432/spring_sample'
export TEST_VARIABLE='application!'

echo "Starting Spring-Sample ${TEST_VARIABLE} "

exec java -jar spring-sample.jar 