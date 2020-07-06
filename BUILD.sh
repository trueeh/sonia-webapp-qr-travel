#!/bin/bash

export JAVA_HOME=`/usr/libexec/java_home -v 11`

rm -fr app.home_IS_UNDEFINED
mvn -Dapp.home=. clean install
