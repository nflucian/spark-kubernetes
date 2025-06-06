ARG SPARK_VERSION=3.5.3
FROM apache/spark:${SPARK_VERSION}

ARG JAR_FILE
COPY ${JAR_FILE} /opt/spark/apps/sparkapp.jar

ENTRYPOINT ["/opt/entrypoint.sh"] 