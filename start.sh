#!/bin/bash

echo "▶ Install chromium and chromedriver..."
apt-get update
apt-get install -y chromium chromium-driver

echo "▶ Building Spring Boot project..."
./gradlew bootJar

echo "▶ Running application..."
java -Dwebdriver.chrome.driver=/usr/bin/chromedriver \
     -jar build/libs/$(ls build/libs | grep '.jar$' | head -n 1)
