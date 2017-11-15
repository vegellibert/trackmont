FROM java:8-alpine

MAINTAINER Danilo Recchia <danilo.recchia@vortus.solutions>

USER root

RUN apk upgrade --update && \
    apk add --update curl bash && \
    rm -rf /var/cache/apk/* && \
    mkdir -p /opt/trackmont/logs && \
    mkdir -p /opt/trackmont/data

ENV JAVA_OPTS -Xms256m -Xmx1024m

COPY ./tmp/trackmont.xml /opt/trackmont/trackmont.xml
COPY ./tmp/default.xml /opt/trackmont/conf/default.xml
COPY ./tmp/schema /opt/trackmont/schema
COPY ./tmp/templates /opt/trackmont/templates
COPY ./tmp/web /opt/trackmont/web
COPY ./tmp/lib /opt/trackmont/lib
COPY ./tmp/trackmont-server.jar /opt/trackmont/trackmont-server.jar

EXPOSE 8082
EXPOSE 5000-5150

WORKDIR /opt/trackmont

ENTRYPOINT ["java","-jar","trackmont-server.jar","trackmont.xml"]
