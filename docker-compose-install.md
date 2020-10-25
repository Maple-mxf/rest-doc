
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

4 启动成功访问
```
http://ip:8432/restdoc
```

5 登录（内置初始化账户）
```
账户：Maple
密码：Maple
```

