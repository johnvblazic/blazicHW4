package homework4

import org.clulab.processors.fastnlp.FastNLPProcessor
import scala.io.Source
import scala.collection.mutable.ListBuffer
/**
  * Created by johnblazic on 4/23/17.
  */
class QLLModel {

  def buildModel(inputFile:String, queryFile:String) {
    val proc = new FastNLPProcessor()

    var docMap = scala.collection.mutable.Map[String, ListBuffer[String]]()
    var countMap = scala.collection.mutable.Map[String, Int]()
    var totalCount = 0

    val inputText = Source.fromFile(inputFile).getLines().toList

    for (line <- inputText) {
      val lineSplit = line.split(":")
      val docID = lineSplit(0)
      val text = lineSplit(1)
      //println(s"Document ID: ${docID}")
      val doc = proc.annotate(text)

      for (s <- doc.sentences) {
        //println(s"Words: ${s.words.mkString(", ")}")
        for (a <- s.lemmas)
          {
            for (x <- a){
              if (!docMap.contains(docID)){
                docMap(docID) = ListBuffer(x)
              } else {
                docMap(docID) += x
              }
              if (!countMap.contains(x)){
                countMap(x) = 1
              } else {
                countMap(x) += 1
              }
              totalCount += 1
            }
          }
      }
    }

    val simpleDocMap = docMap.toMap
    val simpleCountMap = countMap.toMap

    val queryText = Source.fromFile(queryFile).getLines().toList

    //loop through the queries
    for (line <- queryText) {
      val doc = proc.annotate(line)
      //keep the scores for each document, with tuples for each word as (docScore, collectionScore)
      var docScoreMap = scala.collection.mutable.Map[String, ListBuffer[(Double,Double)]]()
      //initialize list that will hold top 4 docs & their scores
      var scoreList = ListBuffer[(String,Double)]()
      //loop through the words in the query
      for (s <- doc.sentences) {
        //loop through lemmas
        for (a <- s.lemmas) {
          //println(s"Query Lemmas: ${a.mkString(" ")}")
          //doubling loop to properly access lemmas
          for (x <- a) {
            //loop through the docs in the input files
            for (doc <- simpleDocMap.keys) {
              val docCount = simpleDocMap(doc).count(_ == x)
              //print(doc + " ")
              //println(docCount)
              val collectionCount = simpleCountMap(x)
              val docLength = simpleDocMap(doc).length
              val Mc = (collectionCount.toDouble / totalCount.toDouble)
              val Md = (docCount.toDouble / docLength.toDouble)
              if (!docScoreMap.contains(doc)){
                docScoreMap(doc) = ListBuffer((Md,Mc))
              } else {
                docScoreMap(doc) += ((Md,Mc))
              }
            }
          }
        }
      }
      //docScoreMap has each doc for the query and the doc and collection scores for each word stored as a tuple

      for (doc <- docScoreMap.keys){
        var Mc = 0.0
        var Md = 0.0

        for (tup <- docScoreMap(doc)){
          Md += tup._1
          Mc += tup._2
        }

        val docScore = ((0.5 * Md) + (0.5 * Mc))
        //print("Doc: " + doc + " : ")
        //println(docScore)

        scoreList += ((doc,docScore))
      }

      val finalList = scoreList.sortWith(_._2 > _._2)

      var iterLen = if (finalList.length > 5) 5 else finalList.length

      println("For the query \"" + line + "\" the (at most) top 5 documents and their scores are:")
      for (i <- 0 until iterLen){
        println(finalList(i)._1 + ": " + finalList(i)._2.toString)
      }
    }
  }

}
