# Spark Application 

This project demonstrates a Spark application that uses Delta Lake for data lake operations and MinIO as an S3-compatible storage backend.

## Prerequisites

- Java 11 or later
- Maven 3.6 or later
- MinIO server running locally (or update configuration for your MinIO server)

## Configuration

The application uses `spark-defaults.properties` for configuration management. This file is located in `src/main/resources/`.

You can modify these settings according to your environment. The configuration priority is:
1. System properties (highest priority)
2. Environment variables
3. Values from `spark-defaults.properties`
4. Default values set in code (lowest priority)


### Add or Override configuration variables

#### Using Environment Variables

You can use environment variables to override Spark configurations. The environment variable name should be the uppercase version of the Spark configuration key with dots replaced by underscores:

```bash
# Set MinIO endpoint using environment variables
export SPARK_HADOOP_FS_S3A_ENDPOINT=http://your-minio-server:9000

# Run the application
spark-submit target/sparkapp-1.0-SNAPSHOT.jar
```

#### Command Line Arguments:

```bash
spark-submit \
  --conf "spark.hadoop.fs.s3a.endpoint=http://your-minio-server:9000" \
  --conf "spark.hadoop.fs.s3a.access.key=your-access-key" \
  --conf "spark.hadoop.fs.s3a.secret.key=your-secret-key" \
  target/sparkapp-1.0-SNAPSHOT.jar
```

#### System Properties:

```bash
spark-submit \
  -Dspark.hadoop.fs.s3a.endpoint=http://minio:9000 \
  -Dspark.executor.memory=2g \
  --class com.example.YourMainClass \
  your-app.jar
```
note: the property should start with `spark`

#### Programmatically:
```scala
override def buildSparkConf(): SparkConf = {
    super.buildSparkConf()
        .set("spark.executor.memory", "2g")
        .set("spark.driver.memory", "1g")
}
```

### Running in Docker and Kubernetes

#### Docker

The application can be containerized using the provided Dockerfile. To build and run the Docker container:

```bash
# Build the JAR file
mvn clean package

# Build the Docker image
docker build -t sparkapp:latest --build-arg JAR_FILE=target/sparkapp-1.0-SNAPSHOT.jar .

# Run the container
docker run -e SPARK_HADOOP_FS_S3A_ENDPOINT=http://host.docker.internal:9000 \
           -e SPARK_HADOOP_FS_S3A_ACCESS_KEY=your-access-key \
           -e SPARK_HADOOP_FS_S3A_SECRET_KEY=your-secret-key \
           sparkapp:latest
```

Note: When running locally, use `host.docker.internal` to access the MinIO server running on your host machine.

#### Kubernetes

The application can be deployed to Kubernetes using the Spark Operator. The `spark-application.yaml` file contains the configuration for running the Spark application in Kubernetes.

Prerequisites:
- Kubernetes cluster
- Spark Operator installed
- MinIO service running in the cluster (or accessible from the cluster)

1. Create a Kubernetes secret for MinIO credentials:
```bash
kubectl create secret generic minio-credentials \
  --from-literal=access-key=your-access-key \
  --from-literal=secret-key=your-secret-key
```

2. Deploy the Spark application:
```bash
kubectl apply -f spark-application.yaml
```

3. Monitor the application:
```bash
kubectl get sparkapplications
kubectl describe sparkapplication sparkapp
kubectl logs -f sparkapp-driver
```

The Spark application configuration in Kubernetes includes:
- Driver and executor resource limits
- S3/MinIO configuration
- Delta Lake extensions
- Application-specific settings

You can modify the `spark-application.yaml` file to adjust:
- Resource allocations (memory, cores)
- Number of executors
- Spark configurations
- MinIO endpoint and credentials
- Application-specific parameters
