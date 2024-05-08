#!/bin/bash

# Spring Boot 서비스 중지 및 컨테이너 삭제
docker-compose down
docker rm  spring-boot-good-relation
docker rmi goodrel-spring

docker-compose up spring
