
# 前提：安装docker和docker-compose

1 下载docker-compose.yml和初始化数据文件
```
cd ~
mkdir restdoc-compose
curl -LJO https://github.com/Open-source-sharing/REST-doc/blob/master/docker-compose.yml
curl -LJO https://github.com/Open-source-sharing/REST-doc/blob/master/mongo-init.js
```

2 安装RESTdoc-console
```
docker-compose up -d
```

3 查看应用启动日志
```
docker-compose logs services
```
