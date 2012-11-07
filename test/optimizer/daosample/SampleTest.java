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
        Option<Campaign> cs = dao.getCampaign("Coda", "Yandex","y1", new DateTime(), new DateTime());
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
        Campaign campaign = dao.getCampaign("Coda", "Yandex","y1", new DateTime(), new DateTime()).get();
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

        //retrieve from DB, check ascending order and conformance to historyStartDate, historyEndDate
        // setting up History Horizon
        Campaign c = dao.getCampaign("Coda", "Yandex","y1",
          date.plusMinutes(25), date.plusMinutes(85)).get();
        java.util.List<Performance> res = c.performanceHistoryJList();

        // check 2 Performances are saved
        assertThat(res.size()).isEqualTo(2);

        // check chronological order and conformance to History Horizon
        assertThat(res.get(0).dateTime()).isEqualTo(performances.get(1).dateTime());
        assertThat(res.get(1).dateTime()).isEqualTo(performances.get(2).dateTime());

      }
    }));
  }


  /** create 1 new BannerPhrase, 1 Banner, 1 Phrase, 1 Region
   * and 2 BannerPhrasePerformances in TestDB_0
   */
  @Test
  public void createBannerPhrasesPerformanceReport() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        // create Mock PeriodType
        PeriodType periodType = new domain.po.PeriodType(1, 1, "");
        // fix start date
        DateTime date = new DateTime();

        // create bannerPhrase
        BannerPhrase bp = new domain.po.BannerPhrase(
            0,
            ScalaGoodies.<Campaign>none(), // i.e. Option[Campaign] = None
            new Some<Banner>(new domain.po.Banner(10, "bb00")),
            new Some<Phrase>(new domain.po.Phrase(10, "pp00", "s")),
            new Some<Region>(new domain.po.Region("rr00")),
            // other ways to create Option[T]
            //Option.<Banner>apply(new domain.po.Banner(10, "bb00")),
            //new Some<Region>(new domain.po.Region(10, "rr00", ScalaGoodies.<Region>none())),
            null,
            null,
            null,
            null
          );

        // create 2 Performances
        Performance performance1 = createPerformance(date, periodType);
        Performance performance2 = createPerformance(date.plusDays(1), periodType);
        // 2 reports = Map[BannerPhrase, Performance]
        java.util.Map<BannerPhrase, Performance> report1 = new java.util.HashMap<BannerPhrase, Performance>();
        report1.put(bp,performance2);
        java.util.Map<BannerPhrase, Performance> report2 = new java.util.HashMap<BannerPhrase, Performance>();
        report2.put(bp,performance1);

        // save BannerPhrasePerformance report in DB
        Dao dao = new SquerylDao();
        Campaign campaign = dao.getCampaign("Coda", "Yandex","y1", new DateTime(), new DateTime()).get();

        // save in DB
        assertThat(dao.createBannerPhrasesPerformanceReport(campaign, report1)).isEqualTo(true);
        assertThat(dao.createBannerPhrasesPerformanceReport(campaign, report2)).isEqualTo(true);


        // check if saved
        //retrieve from DB, check ascending order and conformance to historyStartDate, historyEndDate
        Campaign c = dao.getCampaign("Coda", "Yandex","y1",
          date, date.plusDays(2)).get();

        // find created BannerPhrase
        java.util.List<BannerPhrase> bp_list = c.bannerPhrasesJList();
        java.util.List<BannerPhrase> created_list = new java.util.ArrayList<BannerPhrase>();
        for(BannerPhrase b: bp_list) {
          if(b.banner().get().network_banner_id() == "bb00") created_list.add(b);
        }
        // check there is only created BannerPhrase
        assertThat(created_list.size()).isEqualTo(1);

        // check found BannerPhrase
        BannerPhrase created_bp = created_list.get(0);
        assertThat(created_bp.phrase().get().network_phrase_id()).isEqualTo("pp00");

        // check 2 Performances are created
        assertThat(created_bp.performanceHistoryJList().size()).isEqualTo(2);
        // performanceHistory is in a chronological order
        assertThat(created_bp.performanceHistoryJList().get(0).dateTime()).isEqualTo(date);

      }
    }));
  }


  /** createPermutation(campaign, permutation) should
    * create 1 new Permutation record in TestDB_0
   */
  @Test
  public void createPermutation() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign campaign = dao.getCampaign("Coda", "Yandex","y1", new DateTime(), new DateTime()).get();

        // get BannerPhrases
        java.util.List<BannerPhrase> bp_list = campaign.bannerPhrasesJList();

        // create permutation Map
        java.util.Map<BannerPhrase, Position> perm = new java.util.HashMap<BannerPhrase, Position>();
        for(int i = 0; i < bp_list.size(); i++) {
          perm.put(bp_list.get(i), new domain.po.Position(i));
        }

        // fix start date
        DateTime date = new DateTime();
        // create Permutation
        Permutation permutation = new domain.po.Permutation(date, perm);

        // save in DB
        dao.create(permutation, campaign);

        // check if saved
        //retrieve from DB, check ascending order and conformance to historyStartDate, historyEndDate
        // setting up History Horizon
        Campaign c = dao.getCampaign("Coda", "Yandex","y1",
          date, date.plusMinutes(2)).get();

        // it should be 1 newly created Permutation for the perion 2 minutes
        assertThat(c.permutationHistoryJList().size()).isEqualTo(1);
        // the permutation should have 4 elems
        assertThat(c.permutationHistoryJList().get(0).permutation().size()).isEqualTo(4);
      }
    }));
  }

  /** create Campaign and store it in DB
   */
  @Test
  public void createAndStoreCampaign() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        DateTime startDate = new DateTime();
        DateTime endDate = startDate.plusDays(30);

        // Campaign object
        /*
        java.util.List emptyList = new java.util.ArrayList();
        Campaign cc = new domain.po.Campaign(
          0L,
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
        */

        Campaign cc = new domain.po.Campaign(
          0L,
          "y100",
          startDate,
          new Some<DateTime>(endDate),
          new Some(100.0),
          dao.getUser("Coda"),
          dao.getNetwork("Yandex"),
          null,
          
          // start and end Dates of retrieved Campaign Histories
          startDate,
          endDate,
 
          null,
          null,
          null,
          null,
          null
        );

        Campaign c = dao.create(cc);
        // checking id (db primary key) is created
        assertThat(c.id()).isNotEqualTo(0);

        // check if saved in DB
        Campaign campaign = dao.getCampaign("Coda", "Yandex", "y100",
        		startDate, endDate).get();

        // Histories have to be saved in DB
        assertThat(campaign.budget().get()).isEqualTo(100.0);
        assertThat(campaign.budgetHistoryJList().size()).isEqualTo(1);
        assertThat(campaign.endDateHistory().size()).isEqualTo(1);
      }
    }));
  }

  /** create Curve should
    * create 2 Curves and 2 optimal Permutation in TestDB_0
   */
  @Test
  public void createCurve() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign campaign = dao.getCampaign("Coda", "Yandex","y1", new DateTime(), new DateTime()).get();

        // get BannerPhrases
        java.util.List<BannerPhrase> bp_list = campaign.bannerPhrasesJList();

        // create 2 permutation Maps
        java.util.Map<BannerPhrase, Position> perm1 = new java.util.HashMap<BannerPhrase, Position>();
        java.util.Map<BannerPhrase, Position> perm2 = new java.util.HashMap<BannerPhrase, Position>();
        int n = bp_list.size();
        for(int i = 0; i < n; i++) {
          perm1.put(bp_list.get(i), new domain.po.Position(i));
          perm2.put(bp_list.get(i), new domain.po.Position(n-i));
        }

        // fix start date
        DateTime date = new DateTime();
        // create 2 Permutations
        Permutation permutation2 = new domain.po.Permutation(date.plusMinutes(1), perm2);
        Permutation permutation1 = new domain.po.Permutation(date, perm1);

        // create Curves
        Curve curve1 = new domain.po.Curve(0,1,1,1,1,date, new Some<Permutation>(permutation1));
        Curve curve2 = new domain.po.Curve(0,1,1,1,1,date.plusMinutes(1), new Some<Permutation>(permutation2));

        // save Curves in DB
        dao.create(curve2, campaign);
        dao.create(curve1, campaign);


        // check in DB
        // retrieve from DB, check conformance to historyStartDate, historyEndDate
        Campaign c = dao.getCampaign("Coda", "Yandex","y1",
                date, date.plusMinutes(2)).get();

        // it should be 2 newly created Permutation for the period 2 minutes
        assertThat(c.permutationHistoryJList().size()).isEqualTo(2);
        // it should be 2 newly created Curves
        assertThat(c.curvesJList().size()).isEqualTo(2);
        // curves are in a chronological order
        assertThat(c.curvesJList().get(0).dateTime().isBefore(c.curvesJList().get(1).dateTime()));
        // curves have to have correct optimalPermutations
        assertThat(c.curvesJList().get(0).optimalPermutation().get()).isEqualTo(c.permutationHistoryJList().get(0));
      }
    }));
  }

  /** create Recommendations (Map<BannerPhrase, Bid>)
   * namely creates 4 recommendations (for 4 BannerPhrases) and store it in DB
   */
  @Test
  public void createRecommendation() {
    running(fakeApplication(inMemoryDatabase()), new TestDBHelper( new Runnable() {
      public void run() {
        Dao dao = new SquerylDao();
        Campaign c = dao.getCampaign("Coda", "Yandex","y1", new DateTime(), new DateTime()).get();

        // get BannerPhrases
        java.util.List<BannerPhrase> bp_list = c.bannerPhrasesJList();
        // fix date
        DateTime date = new DateTime();
        // create recommendation Map
        java.util.Map<BannerPhrase, Double> rec_map = new java.util.HashMap<BannerPhrase, Double>();
        for(int i = 0; i < bp_list.size(); i++) {
          rec_map.put(bp_list.get(i), new Double(i+1) );
        }

        //create Recommendation object
        Recommendation recommendation = new domain.po.Recommendation(
          date,
          rec_map
        );

        //store in DB
        dao.create(recommendation);

        // check in DB
        // retrieve from DB, check conformance to historyStartDate, historyEndDate
        Campaign campaign = dao.getCampaign("Coda", "Yandex","y1",
                date, date.plusMinutes(1)).get();

        // now there is 1 recommendation for every BannerPhrase
        for(BannerPhrase bp: campaign.bannerPhrasesJList()){
          assertThat(bp.recommendationHistoryJList().size()).isEqualTo(1);
        }

        //chec the particular recommendation
        RecommendationHistoryElem r = campaign.bannerPhrasesJList().get(0).recommendationHistoryJList().get(0);
        assertThat(r.dateTime()).isEqualTo(date);
        assertThat(r.elem()).isEqualTo(1.0);

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


