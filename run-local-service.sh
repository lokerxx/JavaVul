#!/bin/bash

# 遍历当前目录下以"microservice-"开头的子目录
for dir in microservice-*/ ; do
    # 检查是否存在 pom.xml 文件
    if [[ -f "$dir/pom.xml" ]]; then
        echo "Found pom.xml in $dir"
        # 进入目录
        cd "$dir"
        # 执行 Maven test package
        mvn test package
        # 返回上一级目录
        cd ..
    else
        echo "No pom.xml found in $dir"
    fi
done
docker-compose -f docker-compose-microservice.yml down
docker-compose -f docker-compose-microservice.yml build
docker-compose -f docker-compose-microservice.yml up -d