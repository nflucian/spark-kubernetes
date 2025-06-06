package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.sql.DataFrame

object DeltaMinioApp extends SparkApp with App {

  def generateData(spark: SparkSession, tablePath: String): DataFrame = {
    val data = Seq(
      (1, "Alice", 10.5),
      (2, "Bob", 15.2), 
      (3, "Charlie", 20.0),
      (4, "David", 25.7), 
      (5, "Eve", 30.3)
    )
    spark.createDataFrame(data).toDF("id", "name", "value")
  }
  
  def writeData(df: DataFrame, tablePath: String): Unit = {
    df.write
      .format("delta")
      .mode("overwrite") 
      .save(tablePath)
  }

  def readData(spark: SparkSession, tablePath: String) = {
    spark.read
      .format("delta")
      .option("header", "true")
      .load(tablePath)
  }

  // Main execution
  val tablePath = spark.conf.get("spark.app.s3.table.path")
  val df = generateData(spark, tablePath)
  writeData(df, tablePath)
}