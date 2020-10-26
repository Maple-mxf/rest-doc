# RESTdoc Project
RESTdoc Project是一款提升研发效能的文档软件，集测试与文档生成于一体，快速导入应用的Http API或者RPC API来快速生成文档。

## 工作模式(Work mode)
RESTdoc分为两个模块，RESTdoc-console和RESTdoc-client，您可以选择只使用RESTdoc-console模块，RESTdoc-console跟传统的showdoc其实是
一样的，可以正常编写API文档和WIKI，但是如果您想简化文档的生成，可以选择在您的应用中加速RESTdoc-client的依赖，并且和RESTdoc-console
建立长连接，这样子RESTdoc-console可以快速导入应用的API的文档。

## 架构图(Architecture)
![avatar](https://restdoc.oss-cn-beijing.aliyuncs.com/struct.png)

## 快速安装RESTdoc-console

- [docker-compose一键安装](https://github.com/Open-source-sharing/REST-doc/blob/master/docker-compose-install.md "docker-compose一键安装")
- [源码安装](https://github.com/Open-source-sharing/REST-doc/blob/master/source-install.md "源码安装")


## 快速接入你的应用

1 加入sdk依赖

```
 <dependency>
      <groupId>com.github.open-source-sharing</groupId>
      <version>1.0.RELEASE</version>
      <artifactId>restdoc-dependencies-bom</artifactId>
 </dependency>
```

2 配置application.yml/application.properties

application.yml
```
restdoc:
  host: 127.0.0.1  # RESTdoc-console的IP地址
  port: 4321   # 固定端口
```

application.properties
```
restdoc.host=127.0.0.1  # RESTdoc-console的IP地址
restdoc.port: 4321   # 固定端口
```

3 启动你的应用

## RESTdoc-console线上体验
[RESTdoc线上体验地址](http://152.136.104.144:8432/restdoc/ "RESTdoc线上体验地址")<br>
登录账户: <br>
账户：Maple <br>
密码：Maple



