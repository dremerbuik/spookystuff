package org.tribbloid.spookystuff

import java.util.Properties

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.tribbloid.spookystuff.dsl.driverfactory.NaiveDriverFactory

/**
 * Created by peng on 11/30/14.
 */
abstract class SparkEnvSuite extends FunSuite with BeforeAndAfterAll {

  val conf: SparkConf = new SparkConf().setAppName("test")
    .setMaster("local[*]")

  val sc: SparkContext = {
    val prop = new Properties()
    prop.load(ClassLoader.getSystemResourceAsStream("rootkey.csv"))
    val AWSAccessKeyId = prop.getProperty("AWSAccessKeyId")
    val AWSSecretKey = prop.getProperty("AWSSecretKey")

    val sc = new SparkContext(conf)
    sc.hadoopConfiguration
      .set("fs.s3n.awsAccessKeyId", AWSAccessKeyId)
    sc.hadoopConfiguration
      .set("fs.s3n.awsSecretAccessKey", AWSSecretKey)

    sc
  }
  val sql: SQLContext = new SQLContext(sc)
  val spooky = {

    val spooky: SpookyContext = new SpookyContext(
      sql,
      driverFactory = NaiveDriverFactory(loadImages = true)
    )
    spooky.autoSave = false
    spooky.autoCache = false
    spooky.autoRestore = false

    spooky
  }

  override def finalize(){
    sc.stop()
  }
}