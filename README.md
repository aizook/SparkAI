Spark_DBSCAN
=======
Spark_DBSCAN is a scala open source library of clustering algorithms implemented for Apache Spark

## Installation

Download the jar file located [here](https://s3-eu-west-1.amazonaws.com/aizook/0.0.2-RC/sparkai_2.10-0.0.2-RC.jar) and copy it to your library

## Example
    import org.aizook.scala.clustering.Spark_DBSCAN.Dbscan
    val eps = 3.0
    val minPts = 5
    val data = sc.textFile("data.csv").map(_.split(",")).map(p => (p(0).trim.toDouble, p(1).trim.toDouble)).zipWithUniqueId().map(x => (x._2,x._1)).cache
    val cluster:Dbscan = new Dbscan(eps,minPts,data)
    cluster.predict((data.count+1,(48.3,33.1)))
    
Should return -1 when the tested point fits noise or the cluster id otherwise

## Release Note

#### 0.0.1:
###### New features:
* DBSCAN Algorithm

#### 0.0.2:
###### New features:
* Predict which clusters fit a list of points contained in a RDD
###### Bug fixing:
* [DBSCAN-0001] Predict function returns an exception when the tested point does not belong to a cluster
* luborliu/patch-2 Issue of the algorithm implementation besides the small condition
* luborliu/patch-3 Improve the efficiency of the code
