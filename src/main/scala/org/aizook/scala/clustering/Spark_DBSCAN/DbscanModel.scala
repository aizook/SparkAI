package org.aizook.scala.clustering.Spark_DBSCAN
import org.apache.spark.rdd.RDD;

class DbscanModel  (val eps: Double, val minPts: Double = 5, val points: RDD[(Long, (Double, Double))]) extends Serializable {
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.VertexId;
import org.apache.spark.SparkContext._;

private var model: RDD[(Long, (Long, Double, Double))] = this.train();

def train():RDD[(Long, (Long, Double, Double))] = {

val eps_squared = eps * eps;
val vertex: RDD[(VertexId, (Double, Double))] = points;

val edge:RDD[Edge[Double]] = vertex.cartesian(vertex).map(x => Edge(x._1._1,x._2._1,(x._1._2._1 - x._2._2._1) * (x._1._2._1 - x._2._2._1) + (x._1._2._2 - x._2._2._2) * (x._1._2._2 - x._2._2._2))).filter(x => x.attr <= eps_squared);

val graph = Graph(vertex, edge);

val P = graph.outDegrees.filter(deg => deg._2 >= minPts);
val cluster = Graph(P, graph.edges).connectedComponents().vertices;
val pt_clust = cluster.innerJoin(vertex)((id, clust, p) => (clust,p));
pt_clust.map(x => (x._1.toLong, (x._2._1, x._2._2._1, x._2._2._2)));
}

def predict(point: (Long, (Double, Double))): Long = {
val eps_squared = eps * eps;
val dist:RDD[(Long, Long, Double)] = model.map(x => (x._1, x._2._1, (x._2._2 - point._2._1) * (x._2._2 - point._2._1) + (x._2._3 - point._2._2) * (x._2._3 - point._2._2))).filter(x => x._3 <= eps_squared);  
dist.map(x => (x._2, 1)).reduceByKey(_+_).filter(x => x._2 >= minPts).max()._1
}
}