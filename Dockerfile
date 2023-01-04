FROM tomcat:10.0-jdk17-openjdk-slim
COPY target/*.war /usr/local/tomcat/webapps/sathishjee.war
COPY setenv.sh /usr/local/tomcat
CMD ["sh /usr/setenv.sh"]
EXPOSE 80
CMD ["catalina.sh", "run"]
