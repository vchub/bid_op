#!/bin/bash

HEROKU_URL="http://evening-depths-5210.herokuapp.com"
BASE_URL=$HEROKU_URL"/user/Coda/net/Yandex/camp/y1/reports"

curl -v \
    -H "Content-Type: text/xml" \
    -X POST --data "@report1.xml"  $BASE_URL


