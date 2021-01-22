# RESTdoc Project
RESTdoc Project是一款提升研发效能的API文档工具，无代码侵入，测试用例代码自动生成。

## 开发背景(Development Background)
- API多端对接问题多，增加调试时间成本；
- 编写文档时间成本较高；
- 传统的API文档工具(swagger)对代码有一定的侵入性。

## 工作模式(Work Mode)
RESTdoc分为两个模块，console和client， console是文档可视化工作驾驶舱，您可以只选择console模块进行文档的编写，
如果您想快速把应用的API同步到console，并且可视化展示和测试，那么就需要client来帮助您完成这个需求，只需要按照以下步骤，快速接入您的应用即可。

## 架构图(Architecture)
![avatar](https://restdoc.oss-cn-beijing.aliyuncs.com/struct.png)

## 快速开始(Quick Start)
-  [安装Console](#安装Console)；
-  [增加maven依赖](#增加maven依赖)。

## 安装Console

- [docker-compose一键安装(推荐)](https://github.com/Open-source-sharing/REST-doc/blob/master/docker-compose-install.md "docker-compose一键安装")
- [源码安装](https://github.com/Open-source-sharing/REST-doc/blob/master/source-install.md "源码安装")


## 增加maven依赖

- Maven dependency <br>

普通web应用

```
<dependency>
  <groupId>com.github.open-source-sharing</groupId>
  <artifactId>restdoc-restweb-client</artifactId>
  <version>1.0.RELEASE</version>
</dependency>
<dependency>
  <groupId>org.jetbrains.kotlin</groupId>
  <artifactId>kotlin-reflect</artifactId>
  <version>1.3.72</version>
</dependency>
```

dubbo应用
```
<dependency>
  <groupId>com.github.open-source-sharing</groupId>
  <artifactId>restdoc-dubbo-client</artifactId>
  <version>1.0.RELEASE</version>
</dependency>
<dependency>
  <groupId>org.jetbrains.kotlin</groupId>
  <artifactId>kotlin-reflect</artifactId>
  <version>1.3.72</version>
</dependency>
```


- 配置application.yml/application.properties

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




