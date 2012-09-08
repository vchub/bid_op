# --- First database schema

# --- !Ups

create sequence s_campaign_stats_id;

create table campaign_stats (
  id    bigint DEFAULT nextval('s_campaign_stats_id'),
  name  varchar(128)
);


# --- !Downs

drop table campaign_stats;
drop sequence s_campaign_stats_id;

