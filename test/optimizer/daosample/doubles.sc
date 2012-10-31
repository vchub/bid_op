object doubles {
  val d = new java.lang.Double(1)                 //> d  : java.lang.Double = 1.0
  d.doubleValue()                                 //> res0: Double = 1.0
  val dd:Double = d                               //> dd  : Double = 1.0
  val jd :java.lang.Double = dd                   //> jd  : java.lang.Double = 1.0
	import java.util.Date
	import org.joda.time._
	val dt= new DateTime                      //> dt  : org.joda.time.DateTime = 2012-10-28T06:08:48.951+11:00
	val ddate: Date = new Date()              //> ddate  : java.util.Date = Sun Oct 28 06:08:49 VLAT 2012
	val ddate1: Date = dt.toDate              //> ddate1  : java.util.Date = Sun Oct 28 06:08:48 VLAT 2012
	ddate                                     //> res1: java.util.Date = Sun Oct 28 06:08:49 VLAT 2012
	val dt1: DateTime = new DateTime(ddate)   //> dt1  : org.joda.time.DateTime = 2012-10-28T06:08:49.072+11:00
	
	
	
	
	
	
  
}