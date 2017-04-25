package entry

import homework4.QLLModel

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by johnblazic on 3/10/17.
  */
object Entry {
  def main(args: Array[String]): Unit = {
    val QM = new QLLModel
    QM.buildModel("inputText.txt", "query.txt")
  }

}
