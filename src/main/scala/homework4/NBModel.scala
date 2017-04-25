package homework4

import java.io.File
import java.io.PrintWriter
import scala.math
import org.clulab.processors.fastnlp.FastNLPProcessor
import scala.io.Source
import scala.collection.mutable.Map

/**
  * Created by johnblazic on 4/24/17.
  */
class NBModel {

  def train(folder:String): Unit = {
    val spamTrainFolder = folder + "spam-train/"
    val nonSpamTrainFolder = folder + "nonspam-train/"
    val spamDir = new File(spamTrainFolder)
    val nonspamDir = new File(nonSpamTrainFolder)
    val proc = new FastNLPProcessor()
    //val NB = NBModel

    println(spamTrainFolder)
    println(nonSpamTrainFolder)

    if (spamDir.exists && spamDir.isDirectory) {
      val spamFileList = spamDir.listFiles.filter(_.isFile).toList

      for (f <- spamFileList) {
        NBModel.incYes()
        val input = Source.fromFile(f)
        val inputText = input.getLines().toList

        for (line <- inputText) {
          val doc = proc.annotate(line)
          for (s <- doc.sentences) {
            for (a <- s.lemmas) {
              for (x <- a) {
                NBModel.incYesWord(x)
              }
            }

          }

        }
        input.close()
      }
    } else {
      println("Directories are not formatted correctly, see README")
      System.exit(1)
    }

    if (nonspamDir.exists && nonspamDir.isDirectory) {
      val nonspamFileList = nonspamDir.listFiles.filter(_.isFile).toList

      for (f <- nonspamFileList) {
        NBModel.incNo()
        val input = Source.fromFile(f)
        val inputText = input.getLines().toList

        for (line <- inputText) {
          val doc = proc.annotate(line)
          for (s <- doc.sentences) {
            for (a <- s.lemmas) {
              for (x <- a) {
                NBModel.incNoWord(x)
              }
            }

          }
        }

        input.close()
      }
    } else {
      println("Directories are not formatted correctly, see README")
      System.exit(1)
    }

    println("Yes doc count: " +  NBModel.yesCount.toString)
    println("No doc count: " + NBModel.noCount.toString)
  }

  def classify(folder: String): Unit = {
    println("CLASSIFY, Yes doc count: " +  NBModel.yesCount.toString)
    println("CLASSIFY, No doc count: " + NBModel.noCount.toString)
    val testFolder = folder + "test/"
    val testDir = new File(testFolder)
    val proc = new FastNLPProcessor()

    if (testDir.exists && testDir.isDirectory) {
      val testFileList = testDir.listFiles.filter(_.isFile).toList


      for (f <- testFileList){
        var yesScore = 0.0
        var noScore = 0.0
        val input = Source.fromFile(f)
        val inputText = input.getLines().toList

        for (line <- inputText) {
          val doc = proc.annotate(line)
          for (s <- doc.sentences) {
            for (a <- s.lemmas){
              for (x <-a){
                yesScore += NBModel.getYesScore(x)
                //println(yesScore)
                noScore += NBModel.getNoScore(x)
                //println(noScore)
              }
            }
          }
        }

        yesScore = (math.log(NBModel.yesCount/(NBModel.yesCount + NBModel.noCount)) + yesScore)
        noScore = (math.log(NBModel.yesCount/(NBModel.yesCount + NBModel.noCount)) + noScore)
        if(yesScore >= noScore){
          println("Document ID: " + f.getName + " is SPAM")
          val pw = new PrintWriter(new File(testFolder + f.getName + "-SPAM") )
          pw.write("SPAM")
          pw.close

        } else {
          println("Document ID: " + f.getName + " is NOT SPAM")
          val pw = new PrintWriter(new File(testFolder + f.getName + "-NOTSPAM") )
          pw.write("SPAM")
          pw.close
        }
        //println("Document ID: " + f.getName)
        //println("The SPAM score is: " + yesScore.toString)
        //println("The NOT-SPAM score is: " + noScore.toString)
      }
    } else {
      println("Directories are not formatted correctly, see README")
      System.exit(1)
    }
  }

}

object NBModel {
  var yesMap = scala.collection.mutable.Map[String,Int]()
  var noMap = scala.collection.mutable.Map[String,Int]()
  var yesWordCount = 0
  var noWordCount = 0
  var yesVocabSize = 0
  var noVocabSize = 0
  var noCount = 0
  var yesCount = 0

  private def incYesWord(word:String) = {
    if (!yesMap.contains(word)){
      yesMap(word) = 1
      yesWordCount += 1
      yesVocabSize += 1
    } else {
      yesMap(word) += 1
      yesWordCount += 1
    }
  }

  private def incNoWord(word:String) = {
    if (!noMap.contains(word)){
      noMap(word) = 1
      noWordCount += 1
      noVocabSize += 1
    } else {
      noMap(word) += 1
      noWordCount += 1
    }
  }

  private def incNo() = {
    noCount += 1
  }

  private def incYes() = {
    yesCount += 1
  }

  private def getNoScore(word:String): Double = {
    var theScore = 0.0
    var wCount = if (!noMap.contains(word)) 0 else noMap(word)
    val lnOf2 = scala.math.log(2) // natural log of 2
    def log2(x: Double): Double = scala.math.log(x) / lnOf2
    theScore = log2(((wCount.toDouble + 1.0) / (noWordCount.toDouble + noVocabSize.toDouble)))
    theScore
  }

  private def getYesScore(word:String): Double = {
    var theScore = 0.0
    var wCount = if (!yesMap.contains(word)) 0 else yesMap(word)
    val lnOf2 = scala.math.log(2) // natural log of 2
    def log2(x: Double): Double = scala.math.log(x) / lnOf2
    theScore = log2(((wCount.toDouble + 1.0) / (yesWordCount.toDouble + yesVocabSize.toDouble)))
    theScore
  }
}
