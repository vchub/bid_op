
# Bid Optimizer WebService API



## Общее описание Workflow

1. WebClient сообщает текущую статистику капании за последние 2 дня с определенным интервалом (15 мин.).
1. После получения положительного ответа сервиса, клиент делает паузу (1 мин.)
1. По истечению паузы, клиент обращается к серверу за текущими рекомендациями ставок для ключевых слов/баннеров кампании.
1. Раз в день клиент сообщает детальные отчеты о ходе кампании за прошедшие сутки.


## WebService API


Base_URI = https://api.ws.com/v1/user/:id/net/:id/camp/:id

URI details:
* user/:id - id пользователя, получаемый при регистрации
* net/:id - {yandex, begun, google, ...} - площадка размещения кампании
* camp/:id - id кампании в на рекламной площадке (Яндекс, Google, т.д.)

### Поля сообщений

* CampaignID: Идентификатор кампании.
* StartDate: Дата, за которую приведена статистика. //TODO: 
* EndDate: Дата запроса статистики, включая часы, минуты, секунды (YYYY-MM-DD-HH-MM-SS).
* SumSearch: Стоимость кликов на поиске.
* SumContext: Стоимость кликов в рекламной сети.
* ShowsSearch: Количество показов на поиске.
* ShowsContext: Количество показов в рекламной сети
* ClicksSearch: Количество кликов на поиске.
* ClicksContext: Количество кликов в рекламной сети.
* phraseID: Идентификатор фразы в БД клиента.
* phrase: Текст фразы
* bannerID: Идентификатор объявления.
* regionID: Идентификатор региона показов.

##### Где:

* поиск -  {Yandex, Google}.
* рекланая сеть -  {Yandex, Begun, Google}.
* URI component campaigns/:id == CampaignID



### Сообщение текущей статистики капании
URI: Base_URI/stats

    Request: POST Base_URI/stats
      message body:
      {
          "CampaignID": (int),
          "StartDate": (date),     //YYYY-MM-DD
          "EndDate":  (date),     //YYYY-MM-DD-hh-mm-ss
          "SumSearch": (float),
          "SumContext": (float),
          "ShowsSearch": (int),
          "ShowsContext": (int),
          "ClicksSearch": (int),
          "ClicksContext": (int),
      }


    Responses:
      201 (“Created”) // Сообщение принято. Если новый CampaignID, то так-же создана новая кампания
        message body: {}

      400 (“Bad Request”) // Посланное сообщение содержит ошибки или неполно. Детали в теле Response
        message body: {Errors detals}



### Получение текущих рекомендаций ставок
URI: Base_URI/current_bids

    Request: GET Base_URI/current_bids
    Headers:
      If-Modified-Since: Date Time  // Дата и Время последнего успешного сообщения статистики кампании

Клиент узнает о готовности новых рекомендаций по полю If-Modified-Since - Дата и Время последнего уcпешного POST Base_URI/stats

    Responses:
      304 (“Not Modified”)  // Рекомендации ставок не изменены. Необходимо повторить запрос через интервал (30 секунд)
        Headers:
          Date:
          Last-Modified: Date Time
        message body: {}


      200 (“OK”)  // Новые рекомендации ставок
        Headers:
          Date:
          Last-Modified: Date Time
        message body:
          {
              "CampaignID": (int),
              "StartDate": (date),     //YYYY-MM-DD
              "EndDate":  (date),     //YYYY-MM-DD-hh-mm-ss  TODO: why do we need it?
              "SumSearch": (float),
              "SumContext": (float),
              "ShowsSearch": (int),
              "ShowsContext": (int),
              "ClicksSearch": (int),
              "ClicksContext": (int),
              "BannerPhrase": [
                  {"phraseID": (int), "phrase": (String), "bannerID": (int), "bid": (float)},
                  ....
                  {"phraseID": (int), "phrase": (String), "bannerID": (int), "bid": (float)},
              ]
          }


### Сообщение детального отчета за прошедшие сутки
URI: Base_URI/reports

    Request: POST Base_URI/reports
      message body:
        {
            "StartDate": (date),     //YYYY-MM-DD
            "EndDate":  (date),      //YYYY-MM-DD
            "SumSearch": (float),
            "SumContext": (float),
            "ShowsSearch": (int),
            "ShowsContext": (int),
            "ClicksSearch": (int),
            "ClicksContext": (int),
            "BannerPhrase": [
                {"phraseID": (int), "phrase": (String), "bannerID": (int), "regionID": (int), "bid": (float)},
                ....
                {"phraseID": (int), "phrase": (String), "bannerID": (int), "regionID": (int), "bid": (float)}
            ]
        }


    Responses:
      201 (“Created”) // Сообщение принято. Если новый CampaignID, то так-же создана новая кампания
        message body: {}

      400 (“Bad Request”) // Посланное сообщение содержит ошибки или неполно. Детали в теле Response
        message body: {Errors detals}



