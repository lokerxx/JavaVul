#!/bin/bash

# 遍历两层目录中的所有 Maven 项目
find . -maxdepth 2 -name pom.xml | sort | while read -r pom; do
    dir=$(dirname "$pom")
    echo "Found pom.xml in $dir"
    (
        cd "$dir" || exit 1
        mvn test package
    )
done
docker-compose -f docker-compose-local.yaml down
docker-compose -f docker-compose-local.yaml build
docker-compose -f docker-compose-local.yaml up -d
