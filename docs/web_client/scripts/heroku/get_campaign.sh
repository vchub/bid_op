#!/bin/bash

HEROKU_URL="http://evening-depths-5210.herokuapp.com"
BASE_URL=$HEROKU_URL"/user/Coda/net/Yandex/camp"

echo curl $BASE_URL
curl $BASE_URL

echo -e "\n"

echo curl -v $BASE_URL/y1
curl -v $BASE_URL/y1

echo -e "\n"

# wrong campaign
echo curl -v $BASE_URL/y1000
curl  -v $BASE_URL/y1000

