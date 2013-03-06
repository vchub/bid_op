
-- Old first database schema

# --- !Ups

-- table declarations :
create table "ad_user" (
    "name" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_ad_user_id";
-- indexes on ad_user
create unique index "idx1eb904b2" on "ad_user" ("name");
create table "Network" (
    "name" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_Network_id";
-- indexes on Network
create unique index "idx1ed504b9" on "Network" ("name");
create table "Banner" (
    "id" bigint primary key not null,
    "network_banner_id" varchar(128) not null,
    "campaign_id" bigint not null
  );
create sequence "s_Banner_id";
create table "Region" (
    "parent_id" bigint not null,
    "description" varchar(128) not null,
    "region_id" bigint not null,
    "id" bigint primary key not null,
    "network_region_id" varchar(128) not null
  );
create sequence "s_Region_id";
create table "Phrase" (
    "network_phrase_id" varchar(128) not null,
    "phrase" varchar(128) not null,
    "id" bigint primary key not null
  );
create sequence "s_Phrase_id";
create table "BannerPhrase" (
    "banner_id" bigint not null,
    "region_id" bigint not null,
    "id" bigint primary key not null,
    "phrase_id" bigint not null
  );
create sequence "s_BannerPhrase_id";
create table "Position" (
    "permutation_id" bigint not null,
    "id" bigint primary key not null,
    "position" integer not null,
    "bannerphrase_id" bigint not null
  );
create sequence "s_Position_id";
create table "PeriodType" (
    "description" varchar(128) not null,
    "factor" double precision not null,
    "id" bigint primary key not null
  );
create sequence "s_PeriodType_id";
create table "Curve" (
    "a" double precision not null,
    "id" bigint primary key not null,
    "b" double precision not null,
    "date" timestamp not null,
    "c" double precision not null,
    "optimalPermutation_id" bigint not null,
    "campaign_id" bigint not null,
    "d" double precision not null
  );
create sequence "s_Curve_id";
create table "CampaignPerformance" (
    "cost_search" double precision not null,
    "impress_context" integer not null,
    "impress_search" integer not null,
    "clicks_context" integer not null,
    "clicks_search" integer not null,
    "cost_context" double precision not null,
    "id" bigint primary key not null,
    "date" date not null,
    "periodtype_id" bigint not null,
    "campaign_id" bigint not null
  );
create sequence "s_CampaignPerformance_id";
create table "BannerPhrasePerformance" (
    "cost_search" double precision not null,
    "impress_context" integer not null,
    "impress_search" integer not null,
    "clicks_context" integer not null,
    "clicks_search" integer not null,
    "cost_context" double precision not null,
    "id" bigint primary key not null,
    "date" date not null,
    "periodtype_id" bigint not null,
    "bannerphrase_id" bigint not null
  );
create sequence "s_BannerPhrasePerformance_id";
create table "Permutation" (
    "curve_id" bigint not null,
    "id" bigint primary key not null,
    "date" date not null
  );
create sequence "s_Permutation_id";
create table "ActualBidHistory" (
    "id" bigint primary key not null,
    "date" date not null,
    "bid" double precision not null,
    "bannerphrase_id" bigint not null
  );
create sequence "s_ActualBidHistory_id";
create table "RecommendationHistory" (
    "id" bigint primary key not null,
    "date" date not null,
    "bid" double precision not null,
    "bannerphrase_id" bigint not null
  );
create sequence "s_RecommendationHistory_id";
create table "NetAdvisedBidHistory" (
    "a" double precision not null,
    "id" bigint primary key not null,
    "b" double precision not null,
    "date" date not null,
    "c" double precision not null,
    "bannerphrase_id" bigint not null,
    "d" double precision not null
  );
create sequence "s_NetAdvisedBidHistory_id";
create table "BudgetHistory" (
    "id" bigint primary key not null,
    "date" date not null,
    "budget" double precision not null,
    "campaign_id" bigint not null
  );
create sequence "s_BudgetHistory_id";
create table "EndDateHistory" (
    "endDate" date not null,
    "id" bigint primary key not null,
    "date" date not null,
    "campaign_id" bigint not null
  );
create sequence "s_EndDateHistory_id";
create table "Campaign" (
    "network_id" bigint not null,
    "user_id" bigint not null,
    "network_campaign_id" varchar(128) not null,
    "id" bigint primary key not null,
    "startDate" date not null
  );
create sequence "s_Campaign_id";
-- indexes on Campaign
create unique index "idx9f2b0b23" on "Campaign" ("network_campaign_id");
-- foreign key constraints :
alter table "Banner" add constraint "BannerFK3" foreign key ("campaign_id") references "Campaign"("id");
alter table "Curve" add constraint "CurveFK4" foreign key ("campaign_id") references "Campaign"("id");
alter table "CampaignPerformance" add constraint "CampaignPerformanceFK5" foreign key ("campaign_id") references "Campaign"("id");
alter table "BudgetHistory" add constraint "BudgetHistoryFK6" foreign key ("campaign_id") references "Campaign"("id");
alter table "EndDateHistory" add constraint "EndDateHistoryFK7" foreign key ("campaign_id") references "Campaign"("id");
alter table "BannerPhrase" add constraint "BannerPhraseFK8" foreign key ("banner_id") references "Banner"("id");
alter table "BannerPhrase" add constraint "BannerPhraseFK9" foreign key ("phrase_id") references "Phrase"("id");
alter table "BannerPhrase" add constraint "BannerPhraseFK10" foreign key ("region_id") references "Region"("id");
alter table "Position" add constraint "PositionFK11" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "BannerPhrasePerformance" add constraint "BannerPhrasePerformanceFK12" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "ActualBidHistory" add constraint "ActualBidHistoryFK13" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "RecommendationHistory" add constraint "RecommendationHistoryFK14" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "NetAdvisedBidHistory" add constraint "NetAdvisedBidHistoryFK15" foreign key ("bannerphrase_id") references "BannerPhrase"("id");
alter table "Permutation" add constraint "PermutationFK16" foreign key ("curve_id") references "Curve"("id");
alter table "Position" add constraint "PositionFK17" foreign key ("permutation_id") references "Permutation"("id");
alter table "BannerPhrasePerformance" add constraint "BannerPhrasePerformanceFK18" foreign key ("periodtype_id") references "PeriodType"("id");
alter table "CampaignPerformance" add constraint "CampaignPerformanceFK19" foreign key ("periodtype_id") references "PeriodType"("id");
alter table "Campaign" add constraint "CampaignFK1" foreign key ("user_id") references "ad_user"("id");
alter table "Campaign" add constraint "CampaignFK2" foreign key ("network_id") references "Network"("id");






# --- !Downs
--- TODO: Update 

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



