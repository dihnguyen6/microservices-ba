FROM java:8
COPY target/application-gateway-1.0-SNAPSHOT.jar .
CMD /usr/bin/java -Xmx400m -Xms400m -jar application-gateway-1.0-SNAPSHOT.jar
EXPOSE 8080