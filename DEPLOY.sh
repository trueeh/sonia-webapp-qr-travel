#!/bin/bash

mvn clean install

scp target/qr-travel.jar root@qr.sonia.de:
