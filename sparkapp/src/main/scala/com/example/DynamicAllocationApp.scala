package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.SparkConf

object DynamicAllocationApp extends SparkApp {
  override def appName: String = "DynamicAllocationApp"

    override def buildSparkConf(): SparkConf = {
        super.buildSparkConf()
        .set("spark.dynamicAllocation.enabled", "true")
        .set("spark.shuffle.service.enabled", "true")
        .set("spark.dynamicAllocation.minExecutors", "1")
        .set("spark.dynamicAllocation.maxExecutors", "5")
        .set("spark.dynamicAllocation.initialExecutors", "2")
        .set("spark.dynamicAllocation.executorIdleTimeout", "60s")
        .set("spark.dynamicAllocation.schedulerBacklogTimeout", "1s")
        .set("spark.dynamicAllocation.sustainedSchedulerBacklogTimeout", "1s")
    }

  def main(args: Array[String]): Unit = {
    

    // Create a sample DataFrame with some workload
    val df = spark.range(1, 1000000)
      .withColumn("value", rand() * 100)
      .withColumn("category", (rand() * 10).cast("int"))

    // Perform some operations to generate workload
    val result = df
      .groupBy("category")
      .agg(
        sum("value").as("total"),
        avg("value").as("average"),
        count("*").as("count")
      )
      .orderBy("category")

    // Show the results
    result.write.mode("overwrite")
        .format("delta")
        .save("s3a://datalake/dummy/dynamic/category")
    // Add some delay to observe executor scaling
    Thread.sleep(30000)

    // Perform another operation to generate more workload
    val result2 = df
      .crossJoin(df.limit(1000))
      .groupBy("category")
      .agg(
        sum("value").as("total"),
        avg("value").as("average"),
        count("*").as("count")
      )
      .orderBy("category")

    // Show the results
    result2.write.mode("overwrite")
        .format("delta")
        .save("s3a://datalake/dummy/dynamic/category2")

    // Keep the application running to observe executor scaling
    println("Press Enter to exit...")
    scala.io.StdIn.readLine()
  }
} 