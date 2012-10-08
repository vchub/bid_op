package optimizer;

import java.util.Date;

import org.junit.*;

import domain.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

//application data model


/*
public class SampleTest{

  @Test
  public void simpleCheck() {
      int a = 1 + 1;
      assertThat(a).isEqualTo(2);
  }

  @Test
  public void loosyCheck() {
      String s = "hi";
      assertThat(s).containsIgnoringCase("H");
  }


  //using AppDb - play application in memory DB
  //save and retrieve Campaign from DB
  //@Test
  @Test
  public void findCampaignById() {
    running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {
              User user = (new User("Coda")).put();
              Network network = (new Network("Yandex")).put();
              Campaign camp = new Campaign(user.id(), network.id(), "y4400", new Date(), new Date(), 0);
              assertThat(camp.id()).isEqualTo(0);
              //save in DB
              Campaign c2 = camp.put();
              assertThat(c2.id()).isNotEqualTo(0);
              //get from DB
              Campaign res = camp.get_by_id(c2.id());
              assertThat(res.id()).isNotEqualTo(0);
            }
          });
  }

}

*/

