package optimizer

//import scala.util.Random
import dao.squerylorm._
import dao.squerylorm.test.helpers._
import scala.util.Random
import sun.security.util.Length
import org.squeryl.PrimitiveTypeMode.inTransaction
import scala.collection.Map

object TestSheet {

val e:Option[Long]=Some(10)                       //> e  : Option[Long] = Some(10)

10/2                                              //> res0: Int(5) = 5

  val bannerPhrases = (0 until 2 map
    (i => (0 until 3 map
      (j => (i, j)))))                            //> bannerPhrases  : scala.collection.immutable.IndexedSeq[scala.collection.immu
                                                  //| table.IndexedSeq[(Int, Int)]] = Vector(Vector((0,0), (0,1), (0,2)), Vector((
                                                  //| 1,0), (1,1), (1,2)))

  val v1 = for (i <- 0 to 3; j <- (0 until 3)) yield (i, j)
                                                  //> v1  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((0,0), (0,1
                                                  //| ), (0,2), (1,0), (1,1), (1,2), (2,0), (2,1), (2,2), (3,0), (3,1), (3,2))

  val v2 = (0 to 3) zip (3 to 6)                  //> v2  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((0,3), (1,4
                                                  //| ), (2,5), (3,6))
  val v3 = Map(v2: _*)                            //> v3  : scala.collection.Map[Int,Int] = Map(0 -> 3, 1 -> 4, 2 -> 5, 3 -> 6)
  val v4 = v2.toMap                               //> v4  : scala.collection.immutable.Map[Int,Int] = Map(0 -> 3, 1 -> 4, 2 -> 5, 
                                                  //| 3 -> 6)
  v4.toList                                       //> res1: List[(Int, Int)] = List((0,3), (1,4), (2,5), (3,6))

  val r = new java.util.Random()                  //> r  : java.util.Random = java.util.Random@3e527586

  val r1 = 1 to 1 map (_ => 1 + r.nextGaussian() / 10)
                                                  //> r1  : scala.collection.immutable.IndexedSeq[Double] = Vector(1.1505055998603
                                                  //| 475)
  val r2 = 1 to 1 map (_ => r.nextInt(1))         //> r2  : scala.collection.immutable.IndexedSeq[Int] = Vector(0)

  val q = List(3, 6, 7, 1, 8, 9, 2, 4, 5, 0)      //> q  : List[Int] = List(3, 6, 7, 1, 8, 9, 2, 4, 5, 0)

  val a0 = (0 until 10) zip q                     //> a0  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((0,3), (1,6
                                                  //| ), (2,7), (3,1), (4,8), (5,9), (6,2), (7,4), (8,5), (9,0))
  val b0 = scala.util.Random.shuffle(a0)          //> b0  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((9,0), (8,5
                                                  //| ), (7,4), (5,9), (6,2), (2,7), (0,3), (4,8), (3,1), (1,6))
  //(a0 zip b0).toList.map(._1).sum
  
 val b1 = 0 until b0.length zip b0 map {case (i, (j,k))=>Math.abs(i-j)}
                                                  //> b1  : scala.collection.immutable.IndexedSeq[Int] = Vector(9, 7, 5, 2, 2, 3, 
                                                  //| 6, 3, 5, 8)
  val sumb = (0 /: b1)(_ + _)/2                   //> sumb  : Int = 25

  val result = b0 map { case (ind1, ind2) => ind2 }
                                                  //> result  : scala.collection.immutable.IndexedSeq[Int] = Vector(0, 5, 4, 9, 2,
                                                  //|  7, 3, 8, 1, 6)
  val sumr = (0 /: result)(_ + _)                 //> sumr  : Int = 45

  val q1 = List('d', 'a', 't', 'b')               //> q1  : List[Char] = List(d, a, t, b)
  val q2 = List(3, 5, 2, 1)                       //> q2  : List[Int] = List(3, 5, 2, 1)
  (q1 zip q2).toMap                               //> res2: scala.collection.immutable.Map[Char,Int] = Map(d -> 3, a -> 5, t -> 2
                                                  //| , b -> 1)
val l1 = List(1,2,3)                              //> l1  : List[Int] = List(1, 2, 3)
    val l2 = List(3,2,1)                          //> l2  : List[Int] = List(3, 2, 1)
    //val l3 = (l1,l2) map2 (_*_)
    
    List.map2(l1, l2)(_*_)                        //> res3: List[Int] = List(3, 4, 3)
}