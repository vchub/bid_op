package dao.squerylorm.test.helpers

import dao.squerylorm._

object ClearDB extends AppHelpers {

  def fill_DB() = {
    
    //Networks
    val networks = List("Yandex","Google","Begun")
    networks map (Network(_).put) 
  }

}