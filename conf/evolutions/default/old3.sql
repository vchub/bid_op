
# --- Sample dataset

# --- !Ups

insert into "ad_user" ("id", "name") values
(1, 'Coda'),
(2, 'Some'),
(3, 'Artox');

insert into "Network" ("id", "name") values
(1, 'Google'),
(2, 'Yandex');


# --- !Downs

delete from "Network";
delete from "ad_user";


