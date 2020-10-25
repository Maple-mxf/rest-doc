# 前提：安装java8+和maven
1 克隆源码
```
git clone https://github.com/Open-source-sharing/REST-doc.git
```

2 安装redis
- Ubuntu安装redis
```
sudo apt-get install redis-server
sudo vi /etc/redis.conf
systemctl start redis-server
``` 
- Centos安装redis
```
sudo yum -y install redis-server
sudo vi /etc/redis.conf
systemctl start redis-server
```

3 安装mongodb
- Linux安装mongodb

```
wget https://fastdl.mongodb.org/linux/mongodb-shell-linux-x86_64-rhel70-4.0.20.tgz
tar -zxvf mongodb-shell-linux-x86_64-rhel70-4.0.20.tgz
touch mongo.conf
vi mongo.conf

# 配置mongodb(推荐配置如下)
dbpath=/opt/software/mongodb/data
logpath=/opt/software/mongodb/logs/mongo.log
logappend=true
bind_ip=0.0.0.0
port=37001
fork=true
journal=true
#cpu=true
maxConns=3000
auth=true
```

3 修改RESTdoc-console配置
修改源码项目模块的restdoc-web/src/main/resources/application.yml
将您自己的redis和mongo服务器的配置填入到application.yml中
