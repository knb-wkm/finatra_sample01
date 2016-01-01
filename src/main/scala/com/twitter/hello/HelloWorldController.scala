package com.twitter.hello

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.feature.{HashingTF, IDF}

class HelloWorldController extends Controller {
  val conf = new SparkConf().setAppName("simple application").setMaster("local")
  val sc = new SparkContext(conf)
  val labels = sc.objectFile("model/labels.txt")
  val texts  = sc.objectFile("model/texts.txt")
  val htf = new HashingTF(1000)
  htf.transform(texts)
  val model = NaiveBayesModel.load(sc, "model")

  get("/hi") { request: Request =>
    info("hi")
    "Hello " + request.params.getOrElse("name", "unnamed")
  }

  post("/hi") { hiRequest: HiRequest =>
    val words = hiRequest.word.split(" ")
    val test_tf = htf.transform(words)
    val test = model.predict(test_tf)
    // "label: " + test + " name: " + hiRequest.name + " id: " + hiRequest.id
    // "Hello " + hiRequest.name + " with id " + hiRequest.id
  }
}
