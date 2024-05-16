#!/bin/bash

# 获取所有镜像名:标签，排除含有 <none> 的镜像
images=$(docker images | grep -v '<none>' | awk '/wushang/{print $1":"$2}')

# 循环遍历每个镜像并执行 docker push
for img in $images
do
    echo "Pushing $img..."
    docker push $img
    if [ $? -eq 0 ]; then
        echo "$img pushed successfully"
    else
        echo "Failed to push $img"
    fi
done
