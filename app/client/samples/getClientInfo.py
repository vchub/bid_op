import json, urllib2

#address for sending JSON requests
#url = 'https://soap.direct.yandex.ru/json-api/v4/'
url = 'https://api-sandbox.direct.yandex.ru/json-api/v4/'

#data for OAuth authentication
login = 'vlad.ch01'
token = '8817974ce8b04ad7b8990346c3618f2d'
app_id = 'ee754e0b3d0e4a268e2044d14c112a55'


#input data structure (dictionary)
data = {
   'method': 'GetCampaignsList',
   'login': login,
   'application_id': app_id,
   'token': token,
   'locale': 'en'
   #'param': [login]
}

#convert the dictionary to JSON format and change encoding to UTF-8
jdata = json.dumps(data, ensure_ascii=False).encode('utf8')

#implement the request
response = urllib2.urlopen(url,jdata)

#output the results
print response.read().decode('utf8')

