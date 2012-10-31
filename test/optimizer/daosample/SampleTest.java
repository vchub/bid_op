package optimizer.daosample;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import scala.Option;
import scala.Some;
//import scala.collection.JavaConverters.*;
import scala.collection.immutable.List;
//import scala.collection.JavaConversions.*;

//domain
import domain.*;
import common.*;
//dao
import dao.Dao;
import dao.squerylorm.SquerylDao;


public class SampleTest{


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
    return new domain.po.Performance(
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
    * create 4 CampaignPerformance in TestDB_0
   */
  @Test
  public void createCampaignPerformanceReport() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign campaign = dao.getCampaign("Coda", "Yandex","y1").get();
        // create Mock PeriodType
        PeriodType periodType = new domain.po.PeriodType(1, 1, "");
        // fix start date
        DateTime date = new DateTime();
        //create 4 Performances
        java.util.ArrayList<Performance> performances  = new java.util.ArrayList<Performance>();
        for(int i = 0; i < 4; i++) {
          performances.add(createPerformance(date.plusMinutes(i*30), periodType));
        }

        // save Performances in DB
        for(Performance p: performances){
          dao.createCampaignPerformanceReport(campaign, p);
        }

        //retrieve from DB, check ascending order and in conformance to historyStartDate, historyEndDate
        Campaign c = dao.getCampaign("Coda", "Yandex","y1").get();
        // set up History Horizon
        c.setHistoryStartDate(date.plusMinutes(25));
        c.setHistoryEndDate(date.plusMinutes(85));
        java.util.List<Performance> res = c.performanceHistoryJList();

        // check # of saved
        assertThat(res.size()).isEqualTo(2);

        // check chronological order and conformance to History Horizon
        assertThat(res.get(0).dateTime()).isEqualTo(performances.get(1).dateTime());
        assertThat(res.get(1).dateTime()).isEqualTo(performances.get(2).dateTime());

      }
    }));
  }


  /** getCampaignHistory shoud retrieve CampaignHistory from DB
   */
  /*
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
  */


  /** createBannerPhrasesPerformanceReport(campaign, report) should
    * create 1 new BannerPhrases and BannerPhrasePerformance records in TestDB_0
   */
  /*
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
        java.util.List<BannerPhrase> bp_list = campaign.bannerPhrasesJList();
        //for(int i = 0; i < bp_list.size(); i++) {
        for(BannerPhrase b: bp_list) {
          assertThat(b.performanceHistory().size()).isEqualTo(0);
        }

        // create Performance
        PeriodType periodType = new domain.po.PeriodType(1, 1, "");
        // create bannerPhrase
        BannerPhrase bp = new domain.po.BannerPhrase(
            0,
            //new Some(new domain.po.Banner(10, "bb00")),
            Option.<Banner>apply(new domain.po.Banner(10, "bb00")),
            new Some<Phrase>(new domain.po.Phrase(10, "pp00", "s")),
            new Some<Region>(new domain.po.Region("rr00")),
            //new Some<Region>(new domain.po.Region(10, "rr00", ScalaGoodies.<Region>none())),
            //new Some(new domain.po.Region(10, "rr00", ScalaLang.<Region>none())),
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

      }
    }));
  }
  */


  /** createPermutation(campaign, permutation) should
    * create 1 new Permutation record in TestDB_0
   */
  /*
  @Test
  public void createPermutation() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign c1 = dao.getCampaign("Coda", "Yandex","y1").get();
        Campaign campaign = dao.getCampaignHistory(c1.id(), c1.startDate(), new DateTime()).campaign();

        // get BannerPhrases
        java.util.List<BannerPhrase> bp_list = campaign.bannerPhrasesJList();
        // create permutation Map
        java.util.Map<BannerPhrase, Position> perm = new java.util.HashMap<BannerPhrase, Position>();
        for(int i = 0; i < bp_list.size(); i++) {
          perm.put(bp_list.get(i), new domain.po.Position(i));
        }
        Permutation permutation = new domain.po.Permutation( c1.startDate().plusDays(5),  perm);

        //store in DB
        dao.create(campaign, permutation);

        // check in DB
        Campaign c_res = dao.getCampaignHistory(campaign.id(), campaign.startDate(), campaign.startDate().plusDays(1)).campaign();
        // it should be 2 Permutation (one was prefilled)
        assertThat(c_res.permutationHistoryJList().size()).isEqualTo(2);
        // Permutation #2 should has 4 elems
        assertThat(c_res.permutationHistoryJList().get(1).permutation().size()).isEqualTo(4);

      }
    }));
  }
  */

  /** create Campaign and store it in DB
   */
  /*
  @Test
  public void createAndStoreCampaign() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        DateTime startDate = new DateTime();
        DateTime endDate = startDate.plusDays(30);
        java.util.List emptyList = new java.util.ArrayList();

        // Campaign object
        Campaign cc = new domain.po.Campaign(
          0,
          "y100",
          startDate,
          new Some<DateTime>(endDate),
          new Some(100.0),
          dao.getUser("Coda"),
          dao.getNetwork("Yandex"),

          emptyList,

          emptyList,
          emptyList,
          emptyList,

          emptyList,
          emptyList
        );

        Campaign c = dao.create(cc);
        // checking id (db primary key) is created
        assertThat(c.id()).isNotEqualTo(0);
        // the c by names
        Campaign c_res = dao.getCampaign("Coda", "Yandex", "y100").get();
        // check that we have some
        assertThat(c_res.id()).isEqualTo(c.id());

      }
    }));
  }
  */


  /** create Recommendations (Map<BannerPhrase, Bid>)
   * namely creates 4 recommendations (for 4 BannerPhrases) and store it in DB
   */
  /*
  @Test
  public void createRecommendation() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign c1 = dao.getCampaign("Coda", "Yandex","y1").get();

        //get Campaign
        Campaign campaign = dao.getCampaignHistory(c1.id(), c1.startDate(), new DateTime()).campaign();
        // get BannerPhrases
        java.util.List<BannerPhrase> bp_list = campaign.bannerPhrasesJList();
        // fix date
        DateTime date = new DateTime();
        // create recommendation Map
        java.util.Map<BannerPhrase, Double> rec_map = new java.util.HashMap<BannerPhrase, Double>();
        for(int i = 0; i < bp_list.size(); i++) {
          rec_map.put(bp_list.get(i), new Double(i) );
        }

        //create Recommendation object
        Recommendation recommendation = new domain.po.Recommendation(
          date,
          rec_map
        );

        //store in DB
        dao.create(recommendation);

        // check in DB
        Campaign c_res = dao.getCampaignHistory(campaign.id(), campaign.startDate(), campaign.startDate().plusDays(1)).campaign();
        // now there is 1 recommendation for 1 BannerPhrases
        bp_list = c_res.bannerPhrasesJList();
        for(BannerPhrase bp: bp_list){
          assertThat(bp.recommendationHistoryJList().size()).isEqualTo(1);
        }

        //chec the particular recommendation
        RecommendationHistoryElem r = bp_list.get(0).recommendationHistoryJList().get(0);
        assertThat(r.dateTime()).isEqualTo(date);
        assertThat(r.elem()).isEqualTo(0.0);

      }
    }));
  }
  */




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


