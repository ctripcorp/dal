FROM tomcat
RUN rm -rf /usr/local/tomcat/webapps
ADD ./target/ROOT.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]