# 使用官方 Python 运行时作为父镜像
FROM python:3.9-slim

# 设置工作目录为 /app
WORKDIR /app

# 将当前目录内容复制到位于 /app 中的容器里
COPY . /app

# 安装 requirements.txt 中指定的任何所需软件包
RUN pip install --no-cache-dir -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

# 使端口 5000 可用于此容器外的其他容器
EXPOSE 5000

# 定义环境变量
ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_DEBUG=1

# 使用 flask 命令运行应用程序
CMD ["flask", "run"]
