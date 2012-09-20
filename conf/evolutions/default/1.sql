
# --- First database schema

# --- !Ups

create table ad_user (
  name            varchar(255) not null primary key
);


create table network (
  name            varchar(255) not null primary key
);


create sequence region_seq start with 10000;
create table region (
  id                        bigint not null DEFAULT nextval('region_seq') primary key ,
  region_id                 bigint,
  description               varchar(255) not null
);
alter table region add constraint fk_region_1 foreign key (region_id) references region (id) on delete restrict on update restrict;


create sequence campaign_seq start with 10000;
create table campaign (
  id                  bigint not null DEFAULT nextval('campaign_seq') primary key ,
  user_name           varchar(255) not null,
  network_name        varchar(255) not null,
  network_campaign_id varchar(255) not null
);
alter table campaign add constraint fk_campaign_1 foreign key (user_name) references ad_user(name) on delete restrict on update restrict;
alter table campaign add constraint fk_campaign_2 foreign key (network_name) references network(name);


create sequence phrase_seq start with 10000;
create table phrase (
  id                  bigint not null DEFAULT nextval('phrase_seq') primary key,
  phrase              varchar(511) not null
);


create sequence banner_seq start with 10000;
create table banner (
  id                  bigint not null DEFAULT nextval('banner_seq') primary key,
  campaign_id         bigint not null
);
alter table banner add constraint fk_banner_1 foreign key (campaign_id) references campaign(id) on delete restrict on update restrict;



create sequence bannerphrase_seq start with 10000;
create table bannerphrase (
  id                  bigint not null DEFAULT nextval('bannerphrase_seq') primary key,
  banner_id           bigint not null,
  phrase_id           bigint not null,
  region_id           bigint not null,
  bid                 float not null
);
alter table bannerphrase add constraint fk_bannerphrase_1 foreign key (banner_id) references banner(id) on delete restrict on update restrict;
alter table bannerphrase add constraint fk_bannerphrase_2 foreign key (phrase_id) references phrase(id) on delete restrict on update restrict;
alter table bannerphrase add constraint fk_bannerphrase_3 foreign key (region_id) references region(id) on delete restrict on update restrict;




# --- !Downs

drop table if exists ad_user CASCADE;
drop sequence if exists user_seq;
drop table if exists network CASCADE;
drop sequence if exists network_seq;
drop table if exists campaign CASCADE;
drop sequence if exists campaign_seq;
drop table if exists region CASCADE;
drop sequence if exists region_seq;
drop table if exists bannerphrase CASCADE;
drop sequence if exists bannerphrase_seq;
drop table if exists banner CASCADE;
drop table if exists banner_seq CASCADE;
drop sequence if exists phrase;
drop sequence if exists phrase_seq;


