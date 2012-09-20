
# --- Sample dataset

# --- !Ups

insert into ad_user (name) values
( 'Coda'),
( 'Some');

insert into network values
('Google'),
('Yandex');

insert into region values
(7, null, 'Russia'),
(100, 7, 'Moscow');

insert into campaign (id, user_name, network_name, network_campaign_id) values
(1, 'Coda', 'Yandex', '4400'),
(2, 'Coda', 'Yandex', '4401'),
(3, 'Coda', 'Google', 'go00'),
(4, 'Some', 'Google', 'go10');

insert into banner (id, campaign_id) values
(1, 1),
(2, 1),
(3, 1),
(4, 3);

insert into phrase (id, phrase) values
(1, 'Hi'),
(2, 'Bon jour'),
(3, 'Hello');

insert into bannerphrase (id, banner_id, phrase_id, region_id, bid) values
(1, 1, 1, 7, 1.00),
(2, 2, 1, 7, 2.00),
(3, 3, 1, 100, 3.00),
(4, 1, 2, 7, 1.00);


# --- !Downs

delete from ad_user;
delete from network;
delete from region;
delete from campaign;
delete from banner;
delete from phrase;
delete from bannerphrase;


