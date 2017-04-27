#!/bin/bash

##
## Some variables.
##
HOST="localhost"
PORT="4567"

##
## Post the image and meta-data to the server,
## using two form fields, i.e. image and
## data.
##
curl -i -X POST -H "Content-Type: application/json" -d @../resources/package-descriptor.json http://${HOST}:${PORT}/package-descriptors

echo