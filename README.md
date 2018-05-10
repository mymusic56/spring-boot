# voice2words
convert voice to words，通过spring boot 构建计划任务，在后台循环执行文件下载，
上传录音文件至讯飞服务器，查询转换结果，保存转换结果至数据库
### 创建maven项目目录结构
src/main/java
src/main/resources
src/test/java

### 添加依赖包
通过maven管理依赖包和发布项目

### maven install 依赖包

### 将讯飞sdk打入本地maven仓库
```
mvn install:install-file -Dfile=lfasr-2.0.0.1005.jar -DgroupId=cn.xunfei -DartifactId=xunfei-lfasr -Dversion=2.0.0 -Dpackaging=jar
```
添加依赖：
```
<dependency>  
	        <groupId>cn.xunfei</groupId>  
	        <artifactId>xunfei-lfasr</artifactId>  
	        <version>2.0.0</version>   
</dependency>
```

### maven boot 循环执行任务

### 程序打包
mvn pacakge
将程序打包到.jar中
可运行的jar包在target目录中

### 后台执行

nohup /usr/local/jdk1.8.0_152/bin/java -jar -Dlogging.path=/java/xunfei/logs/  voice2words-0.0.1-SNAPSHOT.jar &
日志写入/java/xunfei/logs/spring.log中
