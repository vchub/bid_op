package optimizer

import breeze.plot._
import breeze.linalg._
import breeze.numerics._
import domain.Curve

class Visualization {

  /** PLOT ----------------------------*/
  def getPlot(_x: Array[Double], _y: Array[Double], fTitle: String = "", fName: String = ""): Unit = {

    val x = DenseVector.apply(_x)
    val y = DenseVector.apply(_y)

    val f = Figure(fName)
    val p = f.subplot(0)
    p += plot(x, y, '.')
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    p.title_=(fTitle)
    f.saveas(fName + ".png")
    f.visible_=(true)
  }

  def getPlot2(
    _x1: Array[Double],
    _y1: Array[Double],
    _x2: Array[Double],
    _y2: Array[Double],
    curves1: domain.Curve,
    curves2: domain.Curve,
    fName: String): Unit = {
    import breeze.plot._

    val x1 = DenseVector(_x1)
    val y1 = DenseVector(_y1)
    val x2 = DenseVector(_x2)
    val y2 = DenseVector(_y2)

    val x3 = linspace(1.0, _x1.length.toDouble, 1000)
    val y3 = DenseVector.ones[Double](x3.length) :/ (
      DenseVector.ones[Double](x3.length) :+ exp(-x3 * curves2.a - DenseVector.ones[Double](x3.length) * curves2.b))

    val f = Figure(fName)

    //----------------------------------
    val p = f.subplot(0)
    p += plot(x1, y1, '.')
    p += plot(x2, y2, '.')

    p += plot(x3, y3, '.')

    p.xlabel = "x axis"
    p.ylabel = "y axis"
    p.title_=("a1=" + curves1.a.toString + " b1=" + curves1.b.toString + "\n a2=" + curves2.a.toString + " b2=" + curves2.b.toString)

    f.saveas(fName + ".png")
    f.visible_=(true)
  }
  /** --------------------------------*/
}