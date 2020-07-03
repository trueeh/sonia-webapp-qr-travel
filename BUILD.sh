#!/bin/bash

rm -fr app.home_IS_UNDEFINED
mvn -Dapp.home=. clean install
