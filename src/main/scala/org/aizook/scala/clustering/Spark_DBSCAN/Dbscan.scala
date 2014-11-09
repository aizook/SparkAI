package org.aizook.scala.clustering.Spark_DBSCAN
import org.apache.spark.rdd.RDD;

/** DBSCAN algorithm in Apache Spark
  *
  * @param eps epsilon (maximum distance between 2 points)
  * @param minPts minimum number of points (Default value = 5)
  * @param points RDD[(Long, (Double, Double))] containing the points to be analyzed
  */
class Dbscan (val eps: Double, val minPts: Double = 5, val points: RDD[(Long, (Double, Double))]) extends Serializable {
import org.apache.spark.SparkContext._;

var model: DbscanModel = new DbscanModel(eps, minPts, points);

/** Train DBSCAN Model
  *
  * @param eps epsilon (maximum distance between 2 points)
  * @param minPts minimum number of points (Default value = 5)
  * @param points RDD[(Long, (Double, Double))] containing the points to be analyzed
  */
def train(p_eps: Double, p_minPts: Double = 5, p_points: RDD[(Long, (Double, Double))]) = {
model = new DbscanModel(p_eps, p_minPts, p_points);
}

/** Get the Identifier of the cluster fitting a given point
  *
  * @param point (Long, (Double, Double)) containing the points to be analyzed
  */
def predict(point: (Long, (Double, Double))): Long = {
model.predict(point);
}
}