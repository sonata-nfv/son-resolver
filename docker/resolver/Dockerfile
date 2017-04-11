##
## This image contains the application for son-resolver.
## It builds on the son-recolver baseimage, which
## contains the OpenJDK JRE and all dependencies.
##
## The base image is created by Maven. It automatically
## copies the dependecies as specified in the POM file
## to the /app directory. Thus, whenever the dependencies
## change, the base image (and all child images) needs
## to be re-build.
##
FROM sonata-nfv/resolver/baseimage
LABEL maintainer michael@bredel.at
RUN mkdir -p /app
COPY ./son-resolver-1.0-SNAPSHOT.jar /app
WORKDIR /app
EXPOSE 4567
CMD ["java", "-jar", "/app/son-resolver-1.0-SNAPSHOT.jar"]
