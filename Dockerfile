FROM tomcat:10.0-jdk17-openjdk-slim

COPY target/*.war /usr/local/tomcat/webapps/sathishjee.war

CMD ["catalina.sh", "run"]
