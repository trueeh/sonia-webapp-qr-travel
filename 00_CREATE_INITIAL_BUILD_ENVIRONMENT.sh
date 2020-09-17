#!/bin/bash

cp -r sample private
mvn clean install
cp target/qr-travel.jar .
./qr-travel.jar --create-sample-config
rm -f ./qr-travel.jar
