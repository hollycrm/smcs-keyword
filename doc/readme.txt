简介：
	该工程主要提供是抓取网页服务。主要封装了httpclient抓取网页、抓取关键字、抓取私信、抓取留言板。
	该工程可打出三个jar,smcs-keyword-0.0.1.jar、smcs-keyword-0.0.1-client.jar、smcs-keyword-0.0.1-jar-with-dependencies.jar。
	把smcs-keyword-0.0.1.jar（程序运行包）、smcs-keyword-0.0.1-client.jar(rmi服务包)包上传到服务器上，smcs-keyword-0.0.1-jar-with-dependencies.jar是
	一个独立就能运行的包目前布置没有用到。

一、打包	
	1、用maven进行打包
		1)mvn clean
		2)mvn compile
		3)mvn rmic:rmic
		3)mvn assembly:assembly
	2、用maven打包后会出现简介中描述的三个包。为布置把smcs-keyword-0.0.1.jar包中的application.properties、applicationContext.xml
	smcs-keyword.properties三个文件删除
	  
二、安装
	1、把smcs-keyword-0.0.1.jar、smcs-keyword-0.0.1-client.jar包拷到打个cmd命令行的那个目录下，如C:\Users\fly。
	2、用maven在本地安装jar包
	   mvn install:install-file -DgroupId=com.hollycrm.smcs -DartifactId=smcs-keyword -Dversion=0.0.1-client -Dpackaging=jar -Dfile=smcs-keyword-0.0.1.jar
	   mvn install:install-file -DgroupId=com.hollycrm.smcs -DartifactId=smcs-keyword -Dversion=0.0.1-client -Dpackaging=jar -Dfile=smcs-keyword-0.0.1-client.jar
		
	3、会在C:\Users\fly\.m2\repository\com\hollycrm\smcs\smcs-keyword\下 的0.0.1-client包和0.0.1包安装。
	
三、布署
    1、用测试服务器上的目录结构做例比
    	-data
    		-test
    			-smcs
    				-smcs-keyword
    					-bin
    					-Jars 
    2、把application.properties、applicationContext.xml、smcs-keyword.properties文件拷到/data/test/smcs/smcs-keyword/bin/目录下
       	在该目录下新建nohuo.out、remove.sh、smcsKeywordStartup.sh
       remove.sh：用来删除日志文件，内容：
       	rm  *.gz
		>nohup.out
		>*.log
       smcsKeywordStartup.sh：用来运行该jar程序，内容：
       	java -cp ".:../Jars/smcs-keyword-0.0.1.jar:../../smcs-core/Jars/smcs-core-0.0.1.jar:../../Jars/lib/*"  -Dfile.encoding=UTF-8 -Djava.rmi.server.codebase="file://`pwd`/../Jars/smcs-keyword-0.0.1-client.jar"  com.hollycrm.smcs.AppLauncher
    3、把smcs-keyword-0.0.1.jar,smcs-keyword-0.0.1-client.jar拷到/data/test/smcs/smcs-keyword/Jars/目录下
    4、运行：nohup./smcsKeywordStartup.sh &
    5、用tail -f nohuo.out查看日记
    6、用ps -ef|grep java查看java程序是否启动。
    					