##
## Makefile for son-resolver and related
## Docker images.
##
## The Makefile exists because I am old and
## crotchety and my fingers can't stop from
## running make commands. Obviously, everthing
## can be done running maven directly.
##

all:
	mvn clean compile

package:
	mvn clean compile package

image-all:
	mvn clean compile docker:build

##
## Create a fresh base image that
## contains all the dependencies
## of the resolver application.
##
image-baseimage:
	@rm -f ../docker/baseimage/*.jar
	@mvn package
	@mvn docker:build@baseimage

image-resolver:
	mvn docker:build@resolver

container-resolver:
	docker create -p 4567:4567 sonata-nfv/resolver/resolver

clean:
	@mvn clean
	@rm -f ../docker/resolver/*.jar
	@rm -f ../docker/baseimage/*.jar


.PHONY: all
