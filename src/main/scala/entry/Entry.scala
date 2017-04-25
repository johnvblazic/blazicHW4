package entry

import homework4.{NBModel, QLLModel}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by johnblazic on 3/10/17.
  */
object Entry {
  def main(args: Array[String]): Unit = {
    if(args.size != 3){
      println("Not enough arguments, 3 expected. Exiting.")
      System.exit(1)
    }

    if (args(2).toString == "QLL"){
      val QM = new QLLModel
      QM.buildModel(args(0).toString, args(1).toString)
    } else if (args(2).toString == "NB"){
      val NB = new NBModel
      NB.train(args(0).toString)
      NB.classify(args(0).toString)
    }
  }

}
