#!/bin/bash

BASE_URL="http://localhost:9000/user/Coda/net/Yandex/camp/y1/recommendations"

curl -v \
    -H "If-Modified-Since: 2012-09-18T10:00:00.000+04:00" \
    $BASE_URL

echo -e "\n\n Now another date so recommendations haven't changed yet \n"

curl -v \
    -H "If-Modified-Since: 2012-09-20T10:00:00.000+04:00" \
    $BASE_URL
