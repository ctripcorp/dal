
#!/bin/bash

/app/apache-tomcat-7.0.52/bin/shutdown.sh
rm -rf /app/apache-tomcat-7.0.52/webapps/ROOT

mkdir /app/apache-tomcat-7.0.52/webapps/ROOT

cd /app/apache-tomcat-7.0.52/webapps/ROOT
jar -xvf dal-dao-gen-0.0.1-SNAPSHOT.war 

rm dal-dao-gen-0.0.1-SNAPSHOT.war

mv /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/web.xml.pub /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/web.xml
mv /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/classes/conf.properties.pub /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/classes/conf.properties
mv /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/classes/jdbc.properties.pub /app/apache-tomcat-7.0.52/webapps/ROOT/WEB-INF/classes/jdbc.properties

/app/apache-tomcat-7.0.52/bin/startup.sh

>>>>>>> cf4d3cdcb09d5b497511ea0ac6d40502460106e3
