#!/bin/bash

BASE_URL="http://localhost:9000/user/Coda/net/Yandex/camp/y1/reports"

curl -v \
    -H "Content-Type: text/xml" \
    -X POST --data "@report1.xml"  $BASE_URL


