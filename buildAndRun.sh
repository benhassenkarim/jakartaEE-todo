#!/bin/sh
mvn clean package && docker build -t com.test/jakartaTodo .
docker rm -f jakartaTodo || true && docker run -d -p 8080:8080 -p 4848:4848 --name jakartaTodo com.test/jakartaTodo
