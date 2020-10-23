springboot docker部署

环境说明
Centos7.0+

打包步骤
1 将Springboot声明为jar类型
@SpringBootApplication
public class OnePushingJarApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnePushingJarApplication.class);
    }
}
2 编写Dockerfile
# Docker image for springboot file run
# VERSION 0.0.1
# Author: eangulee
# 基础镜像使用java
FROM java:8
# 作者
MAINTAINER eangulee <m17793873123@163.com>
# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为app.jar
ADD onepushing-front-cloud-website-api-1.0-SNAPSHOT.jar app.jar
# 运行jar包
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
3 centos7安装docker
# 查询是否安装过docker
rpm -qa|grep docker

# 移除之前安装过的docker
yum remove docker  docker-common docker-selinux docker-engine
 

#更新yum源
yum update -y 
yum install docker -y

# 查看版本
yum list docker --showduplicates | sort -r


4 项目打包上传到服务器


5 设置阿里云docker镜像仓库（可选）
vi /etc/docker/daemon.json
写入以下内容
{
  "registry-mirrors":    ["https://aj2rgad5.mirror.aliyuncs.com"]
}
6 重启docker
systemctl daemon-reload

systemctl restart docker
7 制作docker镜像
docker build -t onepushing-cloud-website:1.0 .
8 查看制作过程

确保不要出错误（如果出错误清掉docker镜像重启docker服务）
9 查看镜像
docker images


10 启动项目（映射端口）注意设定tag的值，也就是版本号
注意：首次启动需要指定端口（非首次启动只需要执行docker run <application_name> -d） -d指定daemon运行
docker run -d -p 30002:8080 onepushing-cloud-website-docker-app:1.0



注意：在启动docker项目之后,会返回一个ID值（container_id）
7652cc5ef3959450c4de79ef4f6beb79c5464390616a902b4e82939a230f2559
11 查看应用日志
docker logs --tail="10" <container_id>

如果看到红线画中的部分 则说明启动成功。

12 Http访问测试
1 开启端口和安全组
firewall-cmd --zone=public --add-port=30001/tcp --permanent 

firewall-cmd --reload

13 docker查看端口映射
1 首先docker ps查看正在运行的容器
docker ps

红线圈起来的部分是容器的ID
2 查看端口
docker port <container_id>

