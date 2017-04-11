FROM sonata-nfv/resolver/baseimage
LABEL maintainer michael@bredel.at
RUN mkdir -p /app
COPY ./son-resolver-1.0-SNAPSHOT.jar /app
WORKDIR /app
EXPOSE 4567
CMD ["java", "-jar", "/app/son-resolver-1.0-SNAPSHOT.jar"]
