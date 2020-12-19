#!/bin/bash

mvn clean compile assembly:single

mkdir release

cp target/nirubot-0.1-jar-with-dependencies.jar release/nirubot.jar

chmod +x release/nirubot