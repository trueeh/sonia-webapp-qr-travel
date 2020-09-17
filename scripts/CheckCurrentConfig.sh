#!/bin/bash

export JAVA_HOME=`/usr/libexec/java_home -v 11`
export PATH="$JAVA_HOME/bin:$PATH"

java -jar target/qr-travel.jar -c
