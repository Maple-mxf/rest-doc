# REST doc 预览版本
[1 RESTdoc开发背景](#RESTdoc开发背景) <br/>
[2 RESTdoc额外功能](#RESTdoc亮点) <br/>
[2 RESTdoc基于CS模式的原理](#RESTdoc基于CS模式的原理)<br/>
[3 RESTdoc目前提供了那些功能](#RESTdoc目前提供了那些功能)<br/>
[4 快速开始](#快速开始)<br/>
[5 后续支持](#后续支持)<br/>
[6 RESTdoc功能展示](#RESTdoc功能展示)<br/>

## RESTdoc开发背景
在软件开发中时常遇到以下的问题

- 快速的编写API文档是具有一定的体力输出，REST doc可以简化切加速这个过程；
- 使用传统的swagger做API文档对于代码造成了很大的侵入性，REST doc可以无缝接入开发者应用并且提供测试控制台；
- 编写一个极为规范的REST web应用是具有一定的挑战的，主要体现在项目的代码从头到尾风格一致，REST doc提供了规范化代码设计方案，并且完全遵循Google REST API规范；
- 编写测试用例是需要花费一定时间的，因为REST doc提供了测试功能，在测试完成之后会依据整个测试过程数据生成多语言测试用例；


## RESTdoc额外功能
- REST doc增加了微服务测试和微服务文档的生成，目前只支持springcloud和dubbo框架；
- REST doc提供了快速构建API文档的功能，不论针对于Http API项目还是Rpc API项目，支持一键导入API并且生成一个半成品的模板；


## RESTdoc基于CS模式的原理
![avatar](https://raw.githubusercontent.com/Overman-mxf/rest-doc/master/static/img/agent-struct.png)

RESTdoc作为一款文档工具，在保证不侵入开发者代码的情况下进行文档的生成与测试，这与swagger是相反的；当在本地开发环境或者
测试环境时，无论使用内网或者外网和RESTdoc控制台建立长连接，都可以达到测试服务API与API文档的自动生成。


## RESTdoc目前提供了那些功能
- HTTP API/Dubbo API/SpringCloud API的在线测试
- HTTP API/Dubbo API/SpringCloud API文档的导入生成
- HTTP API代码测试用例的生成
- 文档字段备注的自动补齐
- 在测试API时，RESTdoc会提示API路径设计


## 快速开始

##### 0 RESTdoc console控制台运行环境说明
[运行环境说明](https://github.com/Overman-mxf/rest-doc/blob/master/runtime_console_env.md)

##### 1 克隆REST doc项目
```
git clone https://github.com/Overman-mxf/rest-doc.git
```

##### 2 构建项目
```
cd rest-doc
mvn clean install -Dmaven.test.skip=true
```

##### 3 运行RESTdoc控制台项目
```
cd rest-doc/restdoc-web/target 
java -jar restdoc-web-1.0-SNAPSHOT.jar
```

##### 4 运行测试客户端应用
```
cd rest-doc/restdoc-client-test/restdoc-client-dubbo-test/target 
java -jar restdoc-client-dubbo-test-1.0-SNAPSHOT.jar
```


##### 5 访问控制台
```
http://127.0.0.1:8432/restdoc/project/view
```
### 后续支持

### 后续2.0支持

目前RESTdoc仅仅处于第一个版本的预览版，部分UI和功能还没有完善；
在未来2.0正式版本会加入以下几个功能
-  提供docker镜像安装与测试
-  微服务网格的监控和测试
-  应用指标的监控
-  各类语言的API opensdk生成
-  兼容swagger并且生成swagger生成文档的JSON
-  兼容spring restdoc，并且生成spring restdoc mock测试用例
-  目前仅提供了spring boot应用的sdk，RESTdoc2.0中新增python应用/go应用的客户端sdk

### RESTdoc功能展示
web/springcloud 文档生成/测试/导入概览图
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/httpdoc-overview.png?raw=true)
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/http-client.png?raw=true)
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/http-test.png?raw=true)
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/code-gen.png?raw=true)


### RESTdoc功能展示
dubbo 文档生成/测试/导入概览图
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/dubbodoc-overview.png?raw=true)
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/dubbo-client.png?raw=true)
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/dubbo-test.png?raw=true)
![avatar](https://github.com/Overman-mxf/rest-doc/blob/master/static/img/dubbo-test-result.png?raw=true)