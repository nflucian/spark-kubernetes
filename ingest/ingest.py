import os
from pyspark.sql import SparkSession
from pyspark.sql.types import *
from pyspark.sql.functions import col, from_json

# Accessing environment variables
kafka_broker = os.environ.get('KAFKA_BROKER', 'localhost:9092')
kafka_topic = os.environ.get('KAFKA_TOPIC', 'demo-topic')
checkpoint_location = os.environ.get('CHECKPOINT_LOCATION', '/checkpoint')
postgres_url = os.environ.get('POSTGRES_URL', 'jdbc:postgresql://localhost:5432/postgres')
postgres_user = os.environ.get('POSTGRES_USER', 'postgres')
postgres_password = os.environ.get('POSTGRES_PASSWORD', 'postgres')
table_name = os.environ.get('POSTGRES_TABLE', 'network')

# Initialize Spark session
spark = SparkSession.builder.appName('ingestapp').getOrCreate()

# Define schema
schema = StructType([
    StructField("device", StringType(), True),
    StructField("email", StringType(), True),
    StructField("ip", StringType(), True),
    StructField("location", StringType(), True),
    StructField("name", StringType(), True),
    StructField("user_id", StringType(), True),
    StructField("action", StringType(), True)
])

# Read data from Kafka
df = (
    spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", kafka_broker)
    .option("subscribe", kafka_topic)
    .option("startingOffsets", "earliest")
    .option("minPartitions", spark.sparkContext.defaultParallelism)
    .load()
)

# Parse JSON data from Kafka
df_cast = (
    df.select(col("value").cast("string").alias("raw_data"))
    .withColumn("data", from_json(col("raw_data"), schema))
    .select("data.*")
)

def write_mini_batch(batch_df, batch_id):
    batch_df.write \
        .format("jdbc") \
        .option("url", postgres_url) \
        .option("dbtable", table_name) \
        .option("user", postgres_user) \
        .option("password", postgres_password) \
        .option("driver", "org.postgresql.Driver") \
        .mode("append") \
        .save()
    
# Write the parsed data to PostgreSQL
job = (
    df_cast.writeStream
    .foreachBatch(write_mini_batch)
    .outputMode("append")
    .option("checkpointLocation", checkpoint_location)
    .start()
)

# Await termination
job.awaitTermination()
