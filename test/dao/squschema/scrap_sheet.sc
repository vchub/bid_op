import scala.collection.immutable.Range

object scrap_sheet {
  /*
  val file_name = "/home/vlad/code/scala/bid_op/test/models/db/schema/xml/report1.xml"
  val node = scala.xml.XML.loadFile(file_name)
  */
  val node = <report>
      <startDate>2012-09-19</startDate>
      <endDate>2012-09-20</endDate>
      <empty></empty>
      <stat>
          <row bannerID="123456"
              phraseID="2"
              position_type="premium"/>
          <row bannerID="223456"
              phraseID="1"
              position_type="premium"/>
      </stat>
    </report>                                     //> node  : scala.xml.Elem = <report>
                                                  //|       <startDate>2012-09-19</startDate>
                                                  //|       <endDate>2012-09-20</endDate>
                                                  //|       <empty></empty>
                                                  //|       <stat>
                                                  //|           <row position_type="premium" bannerID="123456" phraseID="2"></row>
                                                  //| 
                                                  //|           <row position_type="premium" bannerID="223456" phraseID="1"></row>
                                                  //| 
                                                  //|       </stat>
                                                  //|     </report>


for(i<-(node\\"row")) yield {(i\"@bannerID").text.toDouble }
                                                  //> res0: scala.collection.immutable.Seq[Double] = List(123456.0, 223456.0)

/*
for(
  start_date s.startsWith("bannerID")                          //> res9: Boolean = false<-(node\"startDate");
  end_date <-(node\"endDate");
  rows <-(node\\"row")
) yield { (start_date.text, end_date.text, rows.text) }
*/


val n = (node\\"row").head.toString               //> n  : String = <row position_type="premium" bannerID="123456" phraseID="2"></
                                                  //| row>
val nn = n.replace("\"123456\"","\"\"")           //> nn  : java.lang.String = <row position_type="premium" bannerID="" phraseID="
                                                  //| 2"></row>
val nd = xml.XML.loadString(nn)                   //> nd  : scala.xml.Elem = <row phraseID="2" bannerID="" position_type="premium"
                                                  //| ></row>
(node\"stat"\"row") ++ nd                         //> res1: scala.xml.NodeSeq = NodeSeq(<row position_type="premium" bannerID="123
                                                  //| 456" phraseID="2"></row>, <row position_type="premium" bannerID="223456" phr
                                                  //| aseID="1"></row>, <row phraseID="2" bannerID="" position_type="premium"></ro
                                                  //| w>)
error("shouldn't happen")                         //> java.lang.RuntimeException: shouldn't happen
                                                  //| Output exceeds cutoff limit. 
























}
