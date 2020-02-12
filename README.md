# unicom
![https://img.shields.io/badge/language-java-orange.svg?style=flat-square](https://img.shields.io/badge/language-java-yellow.svg?longCache=true&style=popout-square)

不会写东西，直接说一下怎么使用啦。。

演示站：[https://www.iheit.com/ll](https://www.iheit.com/ll)

编译包下载： [https://github.com/kukume/unicom/releases/](https://github.com/kukume/unicom/releases/)

识别验证码使用的是百度Ai的[数字识别](https://ai.baidu.com/tech/ocr_others/numbers)，所以需要申请带数字识别的百度ai的key。

把申请到的apikey和secretkey替换到`application.yml`中

配置文件说明：
![](https://img.kuku.me/links/kuku/126cb0211042025.png)

## 部署

安装java，如是[宝塔](http://bt.cn)或者[oneinstack](https://oneinstack.com)，只需安装`tomcat`即可

解压编译包，把`unicom.jar`和`application.yml`放到同一个文件夹，运行
```shell
java -Djava.security.egd=file:/dev/./urandom -jar unicom.jar
```
即可，持久化运行查阅`screen`的使用，数据库的表将会自动创建，无需导入数据库sql。。

如需域名访问，若为`nginx`,且网站管理程序为`oneinstack`，添加伪静态
```
location / 
{
	proxy_pass  http://127.0.0.1:8099;
	proxy_redirect off;
  proxy_set_header Host $host;
	proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

  proxy_buffering off;
  proxy_cache off;
  proxy_set_header X-Forwarded-Proto $scheme;
}
```
若为宝塔，设置反向代理，且代理地址设置为`http://localhost:8099`即可
