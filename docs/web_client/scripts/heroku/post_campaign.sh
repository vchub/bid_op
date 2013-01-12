#!/bin/bash

HEROKU_URL="http://evening-depths-5210.herokuapp.com"
BASE_URL=$HEROKU_URL"/user/Coda/net/Yandex/camp"

#curl -v -H "Content-Type: application/json" -X PUT --data "@campaign.json"  $BASE_URL
curl -v -H "Content-Type: application/json" -X POST --data "@campaign.json"  $BASE_URL


