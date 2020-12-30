FROM ltvanbinsbergen/eflint:latest

#FROM httpd:2.4 
#COPY my-httpd.conf /usr/local/apache2/conf/httpd.conf
#CMD service apache2 restart

FROM maven:3.6.3-openjdk-11

WORKDIR /tmp/eflint-server/

COPY *.xml .
COPY *.iml .
COPY instance-server/ instance-server 
COPY request-factory/ request-factory
COPY web-server/ web-server

WORKDIR /tmp/eflint-server/web-server

RUN pwd
RUN ls
RUN mvn compile

CMD mvn exec:java -Dexec.mainClass="eflint.Main"
