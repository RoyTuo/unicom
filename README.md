# unicom
![https://img.shields.io/badge/language-java-orange.svg?style=flat-square](https://img.shields.io/badge/language-java-yellow.svg?longCache=true&style=popout-square)

不会写东西，直接说一下怎么使用啦。。

演示站：[https://www.iheit.com/ll](https://www.iheit.com/ll)

编译包下载： [https://github.com/kukume/unicom/releases/](https://github.com/kukume/unicom/releases/)

## 环境
* JDK1.8
* MySQL（如使用mysql）
* nginx/apache（如需域名访问）

快捷方法安装吧。

以宝塔为例：

安装[宝塔](https://bt.cn)
```shell
#centos
yum install -y wget && wget -O install.sh http://download.bt.cn/install/install_6.0.sh && sh install.sh
#ubuntu
wget -O install.sh http://download.bt.cn/install/install-ubuntu_6.0.sh && sudo bash install.sh
#debian
wget -O install.sh http://download.bt.cn/install/install-ubuntu_6.0.sh && bash install.sh
```
打开宝塔面板，只需要在应用商店安装好`tomcat9`就会带有`JDK1.8`环境。
然后在应用商店安装好`mysql`。

## 安装

下载[编译包](https://github.com/kukume/unicom/releases)，更改`application.yml`中的配置

然后把`unicom.jar`和`application.yml`放到同一文件夹下，比如我的文件都在`/root/java`文件下

运行
```shell
cd /root/java
java -jar unicom.jar
```
如需持久化运行，可以使用`screen`
```shell
cd /root/java
screen -dmS unicom java -jar unicom.jar
```
运行之后打开`http://IP地址:8099`即可

如果需要域名访问，在宝塔中添加反向代理，代理地址设置为`http://localhost:8099`即可
