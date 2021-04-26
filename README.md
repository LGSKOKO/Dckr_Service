# Dckr_Service
Dckr_Service项目为Dckr项目提供后端请求相关服务。

## 快速开始

### 服务器运行

```
# 1、Install Kompose
curl -L https://github.com/kubernetes/kompose/releases/download/v1.22.0/kompose-linux-amd64 -o kompose
chmod +x kompose
sudo mv ./kompose /usr/local/bin/kompose

#2、将Dckr_Service-1.0.0.jar拷贝至服务器

#3、输入启动命令（前提需要安装JDK，网上自行查阅资料；确保防火墙已关闭）
nohup java -jar Dckr_Service-1.0.0.jar >>nohup.out 2>&1 &

```

### 容器部署

