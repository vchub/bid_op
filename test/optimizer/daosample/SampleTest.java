package optimizer.daosample;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import scala.Option;
import scala.Some;
import scala.collection.JavaConverters.*;
import scala.collection.immutable.List;
import scala.collection.JavaConversions.*;

//domain
import domain.*;
import common.*;
//dao
import dao.Dao;
import dao.squerylorm.SquerylDao;


public class SampleTest{

  /**
   * creates Campaign (POJO). Any other object of domain can be created in the same way
   * domain.Campaign is an Interface. domain.pojo.Campaign - is plain realization (plain, old, java, object)
   */
  @Test
  public void createCampaign() {
    Campaign c = new domain.pojo.Campaign(0, null, null, null, null, null, null, null, null, null, null, null, null);
    assertThat(c.id()).isEqualTo(0);
  }


  /** using Dao to retrieve Campaigns from created in memory and partially filled DB
   * the DB "content" is in test.dao.squerylorm.test.helpers.TestDB_0.scala
   */
  @Test
  public void getCampaigns() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        // create Dao instance
        Dao dao = new SquerylDao();
        List<Campaign> cs = dao.getCampaigns("Coda", "Yandex");
        assertThat(cs.size()).isEqualTo(2);
      }
    }));
  }

  /** getCampaigns(userName, networkName, networkCampaignId) should
    * get 1 Campaign from TestDB_0
   */
  @Test
  public void getCampaign() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        // create Dao instance
        Dao dao = new SquerylDao();
        Option<Campaign> cs = dao.getCampaign("Coda", "Yandex","y1");
        assertThat(cs.get().network_campaign_id()).isEqualTo("y1");
      }
    }));
  }


  public Performance createPerformance(DateTime date, PeriodType periodType){
    return new domain.pojo.Performance(
      0,
      1,
      1,
      1,
      1,
      1,
      1,
      periodType,
      date
    );
  }

  /** createCampaignPerformanceReport(campaign, performance) should
    * create 1 CampaignPerformance in TestDB_0
   */
  @Test
  public void createCampaignPerformanceReport() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign c = dao.getCampaign("Coda", "Yandex","y1").get();
        PeriodType periodType = new domain.pojo.PeriodType(1, 1, "");
        Performance performance = createPerformance(c.startDate(), periodType);
        Performance perf_res = dao.createCampaignPerformanceReport(c, performance);

        // assert that id == primary key is Not 0
        assertThat(perf_res.id()).isNotEqualTo(0);
      }
    }));
  }


  /** getCampaignHistory shoud retrieve CampaignHistory from DB
   */
  @Test
  public void getCampaignHistory() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign c = dao.getCampaign("Coda", "Yandex","y1").get();
        // get CampaignHistory
        CampaignHistory c_history = dao.getCampaignHistory(c.id(), c.startDate(), new DateTime());
        // CampaignHistory contains Campaign
        Campaign campaign = c_history.campaign();

        // assert that budgetHistory length is 2
        assertThat(campaign.budgetHistory().size()).isEqualTo(2);
        // assert that BannerPhrases length is 4
        assertThat(campaign.bannerPhrases().size()).isEqualTo(4);
        // etc.
        assertThat(campaign.performanceHistory().size()).isEqualTo(2);
        assertThat(campaign.curves().size()).isEqualTo(2);
        assertThat(campaign.permutationHistory().size()).isEqualTo(1);
        // Permutation should has 4 elems
        List<Permutation> ph = campaign.permutationHistory();
        assertThat(ph.head().permutation().size()).isEqualTo(4);
      }
    }));
  }


  /** createBannerPhrasesPerformanceReport(campaign, report) should
    * create 1 new BannerPhrases and BannerPhrasePerformance records in TestDB_0
   */
  @Test
  public void createBannerPhrasesPerformanceReport() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign c1 = dao.getCampaign("Coda", "Yandex","y1").get();
        Campaign campaign = dao.getCampaignHistory(c1.id(), c1.startDate(), new DateTime()).campaign();

        //check initial configuration
        // should be 4 BannerPhrases
        assertThat(campaign.bannerPhrases().size()).isEqualTo(4);
        // should be 0 Performance in any List
        java.util.List<BannerPhrase> bp_list = campaign.bannerPhrasesJ();
        //for(int i = 0; i < bp_list.size(); i++) {
        for(BannerPhrase b: bp_list) {
          assertThat(b.performanceHistory().size()).isEqualTo(0);
        }

        // create Performance
        PeriodType periodType = new domain.pojo.PeriodType(1, 1, "");
        // create bannerPhrase
        BannerPhrase bp = new domain.pojo.BannerPhrase(
            0,
            new Some(new domain.pojo.Banner(10, "bb00")),
            new Some(new domain.pojo.Phrase(10, "pp00", "s")),
            //new Some(new domain.pojo.Region(10, "rr00", ScalaLang.<Region>none())),
            new Some(new domain.pojo.Region("rr00")),
            null,
            null,
            null,
            null
          );
        Performance performance = createPerformance(campaign.startDate(), periodType);
        java.util.Map<BannerPhrase, Performance> report = new java.util.HashMap<BannerPhrase, Performance>();
        report.put(bp,performance);
        // finaly creating
        assertThat(dao.createBannerPhrasesPerformanceReport(campaign, report)).isEqualTo(true);

        /* doesn't work now. Cash is not renewed... can't see immediate changes in DB...
        // check DB now
        Campaign c2 = dao.getCampaignHistory(c1.id(), c1.startDate(), new DateTime()).campaign();
        // should be 5 BannerPhrases
        assertThat(c2.bannerPhrases().size()).isEqualTo(5);
        // cout Performance
        int counter = 0;
        for(BannerPhrase b: c2.bannerPhrasesJ()) {
          counter += b.performanceHistory().size();
        }
        assertThat(counter).isEqualTo(1);
        */

      }
    }));
  }





  /** it's also possible to run fakeApplication with default DB but evoluions (sql scripts defining
   * schema) shoud be in order which is not the case now because the schema is still being changed...
   */
  /*
  @Test
  public void getDao() {
    running(fakeApplication(inMemoryDatabase()), new Runnable() {
      public void run() {
        // create Dao instance
        Dao dao = new SquerylDao();
        assertThat(1).isEqualTo(1);
      }
    });
  }
  */


}


