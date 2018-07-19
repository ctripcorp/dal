FROM registry.cn-hangzhou.aliyuncs.com/acs/maven AS build-env
ENV MY_HOME=/usr/src/app
RUN mkdir -p $MY_HOME
WORKDIR $MY_HOME
COPY . $MY_HOME
RUN mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar -Dfile=$MY_HOME/doc/dependency/ojdbc6_11.2.0.4.jar \
&& mvn install:install-file -DgroupId=com.microsoft.sqlserver -DartifactId=mssql-jdbc -Dversion=6.2.2.jre7.ctrip -Dpackaging=jar -Dfile=$MY_HOME/doc/dependency/mssql-jdbc-6.2.2.jre7.ctrip.jar \
&& mvn install:install-file -DgroupId=javax.servlet -DartifactId=javax.servlet -Dversion=3.0.0.v201103241009 -Dpackaging=jar -Dfile=$MY_HOME/doc/dependency/javax.servlet-3.0.0.v201103241009.jar \
&& cd $MY_HOME/dal-client && mvn clean install -Dmaven.test.skip=true \
&& cd $MY_HOME/dao-gen-core && mvn clean install -Dmaven.test.skip=true \
&& cd $MY_HOME/dal-dao-gen && mvn clean install -Dmaven.test.skip=true

FROM tomcat
RUN rm -rf /usr/local/tomcat/webapps
COPY --from=build-env /usr/src/app/dal-dao-gen/target/ROOT.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]
