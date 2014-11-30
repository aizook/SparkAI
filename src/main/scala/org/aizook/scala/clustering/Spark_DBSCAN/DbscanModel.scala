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

val edge:RDD[Edge[Boolean]] = vertex.cartesian(vertex).map(x => Edge(x._1._1,x._2._1,(x._1._2._1 - x._2._2._1) * (x._1._2._1 - x._2._2._1) + (x._1._2._2 - x._2._2._2) * (x._1._2._2 - x._2._2._2)<=eps_squared)).filter(x => x.attr&&x.srcId!=x.dstId );


val graph = Graph(vertex, edge);

//P are the core points of the clusters
val P = graph.outDegrees.filter(deg => deg._2 >= minPts).innerJoin(vertex)((id,clust,p)=>(id,p)).map(x => (x._1.toLong,(x._2._2._1, x._2._2._2))).cache;


 
// core_edges are the edges which emanate from the core points to all the other points (including core points and boarder points)
// val core_edges = P.cartesian(vertex).map(x=>Edge(x._1._1, x._2._1, true));
// valid_edges are the intersection, which means all the valid edges needed to be considered
//val valid_edges = core_edges.intersection(edge);

val final_edges = P.cartesian(vertex).map(x=>Edge(x._1._1,x._2._1,(x._1._2._1 - x._2._2._1) * (x._1._2._1 - x._2._2._1) + (x._1._2._2 - x._2._2._2) * (x._1._2._2 - x._2._2._2)<=eps_squared)).filter(x => (x.attr && x.srcId!=x.dstId)).cache;


// the final graph needs to be built based on the final edges
val cluster = Graph(P, final_edges).connectedComponents().vertices;
val pt_clust = cluster.innerJoin(vertex)((id, clust, p) => (clust,p));

pt_clust.map(x => (x._1.toLong, (x._2._1, x._2._2._1, x._2._2._2)));
}

def predict(point: (Long, (Double, Double))): Long = {
val eps_squared = eps * eps;
var cluster_max:Long = -1
val dist:RDD[(Long, Long, Double)] = model.map(x => (x._1, x._2._1, (x._2._2 - point._2._1) * (x._2._2 - point._2._1) + (x._2._3 - point._2._2) * (x._2._3 - point._2._2))).filter(x => x._3 <= eps_squared);  
val clusters = dist.map(x => (x._2, 1)).reduceByKey(_+_).filter(x => x._2 >= minPts)
if(clusters.count != 0)
cluster_max = clusters.max()._1
cluster_max
}

def predict(points: RDD[(Long, (Double, Double))]): RDD[(Long, Long)] = {
val eps_squared = eps * eps;
val dist:RDD[((Long, Long), Double)] = points.cartesian(model).map(x => ((x._1._1,x._2._2._1),(x._1._2._1 - x._2._2._2) * (x._1._2._1 - x._2._2._2) + (x._1._2._2 - x._2._2._3) * (x._1._2._2 - x._2._2._3))).filter(x => x._2 <= eps_squared);
val clusters = dist.map(x => (x._1, 1)).reduceByKey(_+_).filter(x => x._2 >= minPts)
val cluster_max = clusters.reduceByKey(math.max(_ , _))
points.map(x => (x._1,x._1)).leftOuterJoin(cluster_max.map(x => x._1)).map(x => (x._1, x._2._2.getOrElse(-1)))
}
}
