package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import java.util.Properties
import scala.jdk.CollectionConverters._
import org.slf4j.LoggerFactory

trait SparkApp {
    def appName: String = getClass.getSimpleName.replaceAll("\\$", "")
    private val logger = LoggerFactory.getLogger(getClass)

    lazy val spark = {
        val sparkConf = buildSparkConf()
        SparkSession.builder()
            .config(sparkConf)
            .getOrCreate()
    }
   
    def buildSparkConf(): SparkConf = {
        val conf = new SparkConf()
            .setIfMissing("spark.master", "local[*]")
            .setIfMissing("spark.app.name", appName)

        // Load configurations from spark-defaults.properties
        loadDefaultsFromFile(conf, "spark-defaults.properties")

        // Load from system properties (these will override the config file)
        sys.props.foreach { case (key, value) =>
            if (key.startsWith("spark.")) conf.set(key, value)
        }

        conf
    }

    private def loadDefaultsFromFile(conf: SparkConf, filename: String): Unit = {
        val configFileStream = getClass.getClassLoader.getResourceAsStream(filename)
        if (configFileStream != null) {
            try {
                logger.info(s"Loading default Spark properties from $filename")
                val props = new Properties()
                props.load(configFileStream)
                props.stringPropertyNames().asScala.foreach { key =>
                    val value = props.getProperty(key)
                    conf.setIfMissing(key, value)
                    logger.debug(s"Loaded default property: $key=$value")
                }
            } catch {
                case e: Exception => logger.error(s"Failed to load properties from $filename", e)
            } finally {
                configFileStream.close()
            }
        } else {
            logger.warn(s"Default properties file '$filename' not found in classpath.")
        }
    }
}
