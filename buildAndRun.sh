#!/bin/sh
mvn clean package && docker build -t com.pedantic/jakarta-ee-jumpstart .
docker rm -f jakarta-ee-jumpstart || true && docker run -d -p 8080:8080 -p 4848:4848 --name jakarta-ee-jumpstart com.pedantic/jakarta-ee-jumpstart 
