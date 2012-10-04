
# --- First database schema

# --- !Ups


-- table declarations :
create table "Banner" (
    "id" bigint primary key not null,
    "network_banner_id" varchar(128) not null,
    "campaign_id" bigint not null
  );
create sequence "s_Banner_id";
create table "BannerPhrase" (
    "banner_id" bigint not null,
    "region_id" bigint not null,
    "id" bigint primary key not null,
    "phrase_id" bigint not null,
    "bid" double precision not null
  );
create sequence "s_BannerPhrase_id";
create table "BannerPhraseStats" (
    "impress_context" integer not null,
    "start_date" date not null,
    "impress_search" integer not null,
    "clicks_context" integer not null,
    "clicks_search" integer not null,
    "sum_context" double precision not null,
    "id" bigint primary key not null,
    "end_date" date not null,
    "sum_search" double precision not null,
    "bannerphrase_id" bigint not null
  );
create sequence "s_BannerPhraseStats_id";
create table "Curve" (
    "start_date" timestamp not null,
    "a" double precision not null,
    "id" bigint primary key not null,
    "b" double precision not null,
    "c" double precision not null,
    "campaign_id" bigint not null,
    "d" double precision not null
  );
create sequence "s_Curve_id";
create table "Network" (
    "name" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_Network_id";
-- indexes on Network
create unique index "idx1ed504b9" on "Network" ("name");
create table "Permutation" (
    "timeslot_id" bigint not null,
    "id" bigint primary key not null,
    "position" integer not null,
    "bid" double precision not null,
    "bannerphrase_id" bigint not null
  );
create sequence "s_Permutation_id";
create table "Phrase" (
    "network_phrase_id" varchar(128) not null,
    "phrase" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_Phrase_id";
create table "Recommendation" (
    "start_date" timestamp not null,
    "id" bigint primary key not null,
    "campaign_id" bigint not null
  );
create sequence "s_Recommendation_id";
create table "Region" (
    "parent_id" bigint not null,
    "description" varchar(128) not null,
    "region_id" bigint not null,
    "id" bigint primary key not null,
    "network_region_id" varchar(128) not null
  );
create sequence "s_Region_id";
create table "TimeSlot" (
    "impress_context" integer not null,
    "start_date" timestamp not null,
    "curve_id" bigint not null,
    "impress_search" integer not null,
    "clicks_context" integer not null,
    "clicks_search" integer not null,
    "sum_context" double precision not null,
    "id" bigint primary key not null,
    "end_date" timestamp not null,
    "timeslottype_id" bigint not null,
    "sum_search" double precision not null
  );
create sequence "s_TimeSlot_id";
create table "TimeSlotType" (
    "description" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_TimeSlotType_id";
create table "ad_user" (
    "name" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_ad_user_id";
-- indexes on ad_user
create unique index "idx1eb904b2" on "ad_user" ("name");
create table "Campaign" (
    "network_id" bigint not null,
    "start_date" date not null,
    "user_id" bigint not null,
    "network_campaign_id" varchar(128) not null,
    "id" bigint primary key not null,
    "end_date" date not null,
    "budget" double precision not null
  );
create sequence "s_Campaign_id";
-- indexes on Campaign
create unique index "idx9f2b0b23" on "Campaign" ("network_campaign_id");
-- foreign key constraints :
alter table "Banner" add constraint "BannerFK3" foreign key ("campaign_id") references "Campaign"("id");
alter table "Curve" add constraint "CurveFK4" foreign key ("campaign_id") references "Campaign"("id");
alter table "Recommendation" add constraint "RecommendationFK5" foreign key ("campaign_id") references "Campaign"("id");
alter table "BannerPhrase" add constraint "BannerPhraseFK6" foreign key ("banner_id") references "Banner"("id");
alter table "BannerPhrase" add constraint "BannerPhraseFK7" foreign key ("phrase_id") references "Phrase"("id");
alter table "BannerPhrase" add constraint "BannerPhraseFK8" foreign key ("region_id") references "Region"("id");
alter table "BannerPhraseStats" add constraint "BannerPhraseStatsFK9" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "Permutation" add constraint "PermutationFK10" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "TimeSlot" add constraint "TimeSlotFK11" foreign key ("curve_id") references "Curve"("id");
alter table "TimeSlot" add constraint "TimeSlotFK12" foreign key ("timeslottype_id") references "TimeSlotType"("id");
alter table "Permutation" add constraint "PermutationFK13" foreign key ("timeslot_id") references "TimeSlot"("id");
alter table "Campaign" add constraint "CampaignFK1" foreign key ("user_id") references "ad_user"("id");
alter table "Campaign" add constraint "CampaignFK2" foreign key ("network_id") references "Network"("id");





# --- !Downs

drop table if exists  Banner                 CASCADE;
drop table if exists  BannerPhrase           CASCADE;
drop table if exists  BannerPhraseStats      CASCADE;
drop table if exists  Campaign               CASCADE;
drop table if exists  Curve                  CASCADE;
drop table if exists  Network                CASCADE;
drop table if exists  Permutation            CASCADE;
drop table if exists  Phrase                 CASCADE;
drop table if exists  Recommendation         CASCADE;
drop table if exists  Region                 CASCADE;
drop table if exists  TimeSlot               CASCADE;
drop table if exists  TimeSlotType           CASCADE;
drop table if exists  ad_user                CASCADE;
drop table if exists  play_evolutions        CASCADE;

drop sequence if exists  s_BannerPhraseStats_id ;
drop sequence if exists  s_BannerPhrase_id      ;
drop sequence if exists  s_Banner_id            ;
drop sequence if exists  s_Campaign_id          ;
drop sequence if exists  s_Curve_id             ;
drop sequence if exists  s_Network_id           ;
drop sequence if exists  s_Permutation_id       ;
drop sequence if exists  s_Phrase_id            ;
drop sequence if exists  s_Recommendation_id    ;
drop sequence if exists  s_Region_id            ;
drop sequence if exists  s_TimeSlotType_id      ;
drop sequence if exists  s_TimeSlot_id          ;
drop sequence if exists  s_ad_user_id           ;
drop sequence if exists  user_seq               ;



