#!/bin/bash

rm -rf ~/jetty-distribution-8.1.14.v20131031/webapps/root

mkdir ~/jetty-distribution-8.1.14.v20131031/webapps/root

cd ~/jetty-distribution-8.1.14.v20131031/webapps/root
jar -xvf dal-dao-gen-0.0.1-SNAPSHOT.war 

rm dal-dao-gen-0.0.1-SNAPSHOT.war

mv ~/jetty-distribution-8.1.14.v20131031/webapps/root/WEB-INF/web.xml.pub ~/jetty-distribution-8.1.14.v20131031/webapps/root/WEB-INF/web.xml
mv ~/jetty-distribution-8.1.14.v20131031/webapps/root/WEB-INF/classes/conf.properties.pub ~/jetty-distribution-8.1.14.v20131031/webapps/root/WEB-INF/classes/conf.properties
java -jar ~/jetty-distribution-8.1.14.v20131031/start.jar &

mv /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/web.xml.pub /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/web.xml
mv /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/classes/conf.properties.pub /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/classes/conf.properties