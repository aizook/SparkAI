Spark_DBSCAN
=======
Spark_DBSCAN is a scala open source library of clustering algorithms implemented for Apache Spark

## Installation

Download the jar file located [here](https://s3-eu-west-1.amazonaws.com/aizook/0.0.2-RC/sparkai_2.10-0.0.2-RC.jar) and copy it to your library

## Example
    import org.aizook.scala.clustering.Spark_DBSCAN.DBSCAN
    val data = sc.textFile("data.csv").map(_.split(",")).map(p => (p(0).trim.toDouble, p(1).trim.toDouble)).zipWithUniqueId().map(x => (x._2,x._1)).cache
    val cluster:Dbscan = new Dbscan(3,5,data)
    cluster.predict((2000,(48.3,33.1)))

## Release Note

#### 0.0.1:
###### New features:
* DBSCAN Algorithm

#### 0.0.2:
###### Bug fixing:
* [DBSCAN-0001] Predict function returns an exception when the tested point does not belong to a cluster
