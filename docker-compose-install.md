
# 前提：安装docker和docker-compose

-1 [安装docker和docker-compose](https://github.com/Open-source-sharing/REST-doc/blob/master/docker-install.md "安装docker和docker-compose")

0 配置docker加速器您可以通过修改daemon配置文件/etc/docker/daemon.json来使用加速器（可选步骤）
```
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://hksq8cy4.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```


1 下载docker-compose.yml和初始化数据文件
```
cd ~
mkdir restdoc-compose
curl -LJO https://github.com/Open-source-sharing/REST-doc/blob/master/docker-compose.yml
curl -LJO https://github.com/Open-source-sharing/REST-doc/blob/master/mongo-init.js
```

2 安装RESTdoc-console并且后台启动
```
docker-compose up -d
```

3 查看应用启动日志
```
docker-compose logs -f
```

4 启动成功访问
```
http://ip:8432/restdoc
```

5 登录（内置初始化账户）
```
账户：Maple
密码：Maple
```

