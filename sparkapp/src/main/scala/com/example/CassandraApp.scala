package com.example

import org.apache.hadoop.shaded.org.eclipse.jetty.websocket.common.frames.DataFrame

object CassandraApp extends SparkApp with App {
  
  val keyspace = spark.conf.get("spark.app.cassandra.keyspace", "default")
  val table = spark.conf.get("spark.app.cassandra.table", "network")
  val postgres_url = spark.conf.get("spark.app.postgres.url", "jdbc:postgresql://localhost:5432/postgres")
  val postgres_user = spark.conf.get("spark.app.postgres.user", "postgres")
  val postgres_password = spark.conf.get("spark.app.postgres.password", "postgres")
  val table_name = spark.conf.get("spark.app.postgres.table", "network")

  val df = spark.read
      .format("org.apache.spark.sql.cassandra")
      .option("keyspace", keyspace)
      .option("table", table)
      .load()

  val outDF = df.where("country = 'RU' AND lastUpdated > '2025-06-05'")  

  outDF.write
    .format("jdbc")
    .option("url", postgres_url)
    .option("dbtable", table_name)
    .option("user", postgres_user)
    .option("password", postgres_password)
    .option("driver", "org.postgresql.Driver")
    .mode("append")
    .save()
}
