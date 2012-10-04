#!/bin/bash

BASE_URL="http://localhost:9000/user/Coda/net/Yandex/camp"

#curl -v -H "Content-Type: application/json" -X PUT --data "@campaign.json"  $BASE_URL
curl -v -H "Content-Type: application/json" -X POST --data "@campaign.json"  $BASE_URL


