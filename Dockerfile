#build
FROM openjdk:8 as builder
RUN apt-get update && apt-get install -y unzip
WORKDIR /opt/gradle
RUN curl -L https://services.gradle.org/distributions/gradle-6.7-bin.zip -o gradle-6.7-bin.zip
RUN unzip gradle-6.7-bin.zip
ENV GRADLE_HOME=/opt/gradle/gradle-6.7
ENV PATH=$PATH:$GRADLE_HOME/bin
RUN gradle --version
RUN mkdir -p /tmp/lf-cdn-api
ADD . /tmp/lf-cdn-api
RUN /opt/gradle/gradle-6.7/bin/gradle -b /tmp/lf-cdn-api/build.gradle --refresh-dependencies clean test build
RUN cp /tmp/lf-cdn-api/build/libs/account-management*.jar /tmp/account-management-service.jar

#main
FROM openjdk:8-slim
RUN apt-get update
RUN apt-get install -y \
    supervisor \ 
    python-pip 
RUN pip install awscli
RUN mkdir -p /opt/dlvr/ams/
RUN mkdir -p /storage/dlvr/ams/logs

#logs
RUN ln -sf /dev/stdout /storage/dlvr/ams/logs/ams-stdout.log \
        && ln -sf /dev/stderr /storage/dlvr/ams/logs/ams-stderr.log

RUN ln -s /usr/local/openjdk-8/bin/java /usr/bin/java
ADD supervisord.conf /etc/supervisor/supervisord.conf
ADD supervisord.ams.conf /etc/supervisor/conf.d/supervisord.ams.conf
COPY --from=builder /tmp/lf-cdn-api/src/main/resources/* /opt/dlvr/ams/
COPY --from=builder /tmp/account-management-service.jar /opt/dlvr/ams/
CMD ["supervisord", "-n", "-c", "/etc/supervisor/supervisord.conf"]
