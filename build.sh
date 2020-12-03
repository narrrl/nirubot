#!/bin/bash

gradle build

rm build/libs/nirubot
touch build/libs/nirubot

echo '#!/usr/bin/java -jar' > build/libs/nirubot
cat build/libs/nirubot-0.1-all.jar >> build/libs/nirubot

chmod +x build/libs/nirubot

./build/libs/nirubot
