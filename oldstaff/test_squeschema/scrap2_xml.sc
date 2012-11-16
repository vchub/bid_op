import scala.collection.immutable.Range

object scrap_sheet {
  val node = <report>
      <startDate>2012-09-19</startDate>
      <endDate>2012-09-20</endDate>
      <empty></empty>
      <stat>
          <row bannerID="123456" phraseID="2" position_type="premium"/>
          <row bannerID="223456" phraseID="1" position_type="premium"/>
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


val n = (node\\"row").head.toString               //> n  : String = <row position_type="premium" bannerID="123456" phraseID="2"></
                                                  //| row>
val nn = n.replace("\"123456\"","\"\"")           //> nn  : java.lang.String = <row position_type="premium" bannerID="" phraseID="
                                                  //| 2"></row>
val nd = xml.XML.loadString(nn)                   //> nd  : scala.xml.Elem = <row phraseID="2" bannerID="" position_type="premium"
                                                  //| ></row>
(node\"stat"\"row") ++ nd                         //> res0: scala.xml.NodeSeq = NodeSeq(<row position_type="premium" bannerID="123
                                                  //| 456" phraseID="2"></row>, <row position_type="premium" bannerID="223456" phr
                                                  //| aseID="1"></row>, <row phraseID="2" bannerID="" position_type="premium"></ro
                                                  //| w>)
import xml.NodeSeq

def change_bannerID(node: NodeSeq, pattern: String, new_content: String): NodeSeq = {
  val buf = node.toString
  val res = buf.replaceFirst(pattern, new_content)
  xml.XML.loadString(res)
}                                                 //> change_bannerID: (node: scala.xml.NodeSeq, pattern: String, new_content: Str
                                                  //| ing)scala.xml.NodeSeq
                                                  //| Output exceeds cutoff limit. 
val pattern = "bannerID=\".*\""
val new_node = change_bannerID(node, pattern, "")


println(new_node)





















}