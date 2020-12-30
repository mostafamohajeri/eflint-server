FROM ltvanbinsbergen/eflint:latest AS executable 

FROM maven:3.6.3-openjdk-11

COPY --from=executable /root/.cabal/bin/eflint-server /usr/bin/

WORKDIR /tmp/eflint-server/

COPY *.xml .
COPY *.iml .
COPY instance-server/ instance-server 
COPY request-factory/ request-factory
COPY web-server/ web-server

WORKDIR /tmp/eflint-server/web-server

RUN mvn compile

CMD mvn exec:java -Dexec.mainClass="eflint.Main"
