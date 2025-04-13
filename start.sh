#!/bin/bash

# 1. 크롬드라이버 다운로드 (버전에 맞게 변경 가능)
CHROMEDRIVER_VERSION=122.0.6261.111
wget -N https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROMEDRIVER_VERSION}/linux64/chromedriver-linux64.zip

# 2. 압축 풀기
unzip chromedriver-linux64.zip
mv chromedriver-linux64/chromedriver /usr/bin/chromedriver
chmod +x /usr/bin/chromedriver

# 3. Chromium 브라우저도 다운로드 (필요 시)
wget -N https://commondatastorage.googleapis.com/chromium-browser-snapshots/Linux_x64/418926/chrome-linux.zip
unzip chrome-linux.zip
mv chrome-linux /usr/bin/chromium
chmod +x /usr/bin/chromium/chrome


java -jar build/libs/$(ls build/libs | grep '.jar$' | head -n 1)
