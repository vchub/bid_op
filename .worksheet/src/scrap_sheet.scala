import scala.collection.immutable.Range

object scrap_sheet {import scala.runtime.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(366); 
  val node = <report>
      <startDate>2012-09-19</startDate>
      <endDate>2012-09-20</endDate>
      <empty></empty>
      <stat>
          <row bannerID="123456" phraseID="2" position_type="premium"/>
          <row bannerID="223456" phraseID="1" position_type="premium"/>
      </stat>
    </report>;System.out.println("""node  : scala.xml.Elem = """ + $show(node ));$skip(38); 


val n = (node\\"row").head.toString;System.out.println("""n  : String = """ + $show(n ));$skip(40); 
val nn = n.replace("\"123456\"","\"\"");System.out.println("""nn  : java.lang.String = """ + $show(nn ));$skip(32); 
val nd = xml.XML.loadString(nn);System.out.println("""nd  : scala.xml.Elem = """ + $show(nd ));$skip(26); val res$0 = 
(node\"stat"\"row") ++ nd
import xml.NodeSeq;System.out.println("""res0: scala.xml.NodeSeq = """ + $show(res$0));$skip(211); 

def change_bannerID(node: NodeSeq, pattern: String, new_content: String): NodeSeq = {
  val buf = node.toString
  val res = buf.replaceFirst(pattern, new_content)
  xml.XML.loadString(res)
};System.out.println("""change_bannerID: (node: scala.xml.NodeSeq, pattern: String, new_content: String)scala.xml.NodeSeq""");$skip(32); 
val pattern = "bannerID=\".*\"";System.out.println("""pattern  : java.lang.String = """ + $show(pattern ));$skip(50); 
val new_node = change_bannerID(node, pattern, "");System.out.println("""new_node  : scala.xml.NodeSeq = """ + $show(new_node ));$skip(20); 


println(new_node)}





















}