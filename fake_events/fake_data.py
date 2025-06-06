from pyspark.sql import SparkSession
from faker import Faker
import time
import os

# Accessing environment variables
KAFKA_BROKER = os.environ.get('KAFKA_BROKER', 'localhost:9092')
KAFKA_TOPIC = os.environ.get('KAFKA_TOPIC', 'network-logs')
CHECKPOINT_LOCATION = os.environ.get('CHECKPOINT_LOCATION', '/checkpoint')

# Initialize Faker
faker = Faker()

# Initialize Spark
spark = (
    SparkSession.builder
    .appName("FakerDataset")
    .config("spark.sql.shuffle.partitions", "1")
    .getOrCreate()
)


def generate_fake_event():
    """Generates a random user event."""
    return {
        "user_id": faker.uuid4(),
        "name": faker.name(),
        "email": faker.email(),
        "ip": faker.ipv4(),
        "action": faker.random_element(elements=["click", "purchase", "view", "login"]),
        "device": faker.random_element(elements=["mobile", "desktop", "tablet"]),
        "location": faker.city()
    }


# Generate a DataFrame of fake data
def create_fake_df(spark, num_records=100):
    fake_data = [generate_fake_event() for _ in range(num_records)]
    return spark.createDataFrame(fake_data)


while True:
    df = create_fake_df(spark, num_records=10)

    # Convert to Kafka-compatible JSON format
    kafka_df = df.selectExpr("CAST(user_id AS STRING) AS key",
                             "to_json(struct(*)) AS value")

    # Write to Kafka
    kafka_df.write \
        .format("kafka") \
        .option("kafka.bootstrap.servers", KAFKA_BROKER) \
        .option("topic", KAFKA_TOPIC) \
        .option("checkpointLocation", CHECKPOINT_LOCATION) \
        .save()

    time.sleep(10)
