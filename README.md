# REST doc
[为什么需要REST doc](#为什么需要RESTdoc) <br/>
[快速开始](#快速开始)


##为什么需要RESTdoc
REST doc主要解决的问题如下

- 快速的编写API文档是具有一定的体力输出，REST doc可以简化切加速这个过程；
- 使用传统的swagger做API文档对于代码造成了很大的侵入性，REST doc可以无缝接入开发者应用并且提供测试控制台；
- 编写一个极为规范的REST web应用是具有一定的挑战的，主要体现在项目的代码从头到尾风格一致，REST doc提供了规范化代码设计方案，并且完全遵循Google REST API规范；
- 编写测试用例是需要花费一定时间的，因为REST doc提供了测试功能，在测试完成之后会依据整个测试过程数据生成多语言测试用例；
- REST doc增加了微服务测试和微服务文档的生成，目前只支持springcloud和dubbo框架；
- REST doc提供了快速构建API文档的功能，不论针对于Http API项目还是Rpc API项目，支持一键导入API并且生成一个半成品的模板；


##快速开始