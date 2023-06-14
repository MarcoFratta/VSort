package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}
import model.Sortable

class SortingTest extends AnyFlatSpec with Matchers:

  import model.Sortable.*

  given Comparable[Int] with
    override def compare(a: Int, b: Int): Boolean = a - b > 0

  "Bubble sort" should "work" in {
    var mList1 = Sortable(5, 4, 2, 1, 3)
    for (_ <- 0 to mList1.length() - 2) {
      for (j <- 0 to mList1.length() - 2) {
        mList1 = mList1.compare(j, j + 1)(x => x.swap(j, j + 1).get)(x => x).get
      }
    }
    mList1.data shouldBe Seq(1, 2, 3, 4, 5)
  }

  "Insertion sort" should "work" in {
    var mList = Sortable(5, 4, 2, 1, 3)
    for (i <- 0 to mList.length() - 2) {
      mList = mList.select("min", i).get
      for (j <- i + 1 until mList.length()) {
        mList = mList.compare(mList.getSelection("min"), j)(x => x.select("min", j).get)(x => x).get
      }
      mList = mList.swap(mList.getSelection("min"), i).get
      mList = mList.deselect("min").get
    }
    mList.data shouldBe Seq(1, 2, 3, 4, 5)
  }