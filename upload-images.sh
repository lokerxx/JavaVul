#!/bin/bash
#构建和上传agent镜像
cd SimpleAgent
javac -source 1.8 -target 1.8 -d . src/main/java/my/agent/SimpleAgent.java
jar cvfm SimpleAgent.jar MANIFEST.MF my/agent/SimpleAgent.class
cp SimpleAgent.jar ../agent/agent.jar
docker build -t wushangleon/sec_agent .
docker push wushangleon/sec_agent
cd ../

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
