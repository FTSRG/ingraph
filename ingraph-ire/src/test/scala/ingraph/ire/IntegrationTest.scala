package ingraph.ire

import hu.bme.mit.ire.TransactionFactory
import org.scalatest.FlatSpec

import scala.io.Source

// in Eclipse / ScalaTest, open the run configuration, go to the Arguments tab and set the
// Working directory to Other: ${workspace_loc:ingraph}
class IntegrationTest extends FlatSpec {
  val modelPath = "../trainbenchmark/models/railway-repair-1-tinkerpop.graphml"
  def queryPath(query: String): String = s"queries/trainbenchmark/$query.cypher"
  case class TestCase(name: String, expectedResultSize: Int)

  Vector(
    TestCase("PosLength", 95),
    TestCase("RouteSensor", 18),
    TestCase("SemaphoreNeighbor", 3),
    TestCase("SwitchMonitored", 0),
    TestCase("SwitchSet", 5)
  ).foreach(
    t => t.name should "work" in {
      val query = Source.fromFile(queryPath(t.name)).getLines().mkString
      val adapter = new IngraphAdapter(query)
      val tf = new TransactionFactory(16)
      tf.subscribe(adapter.engine.inputLookup)
      val tran = tf.newBatchTransaction()

      adapter.readGraph(modelPath, tran)
      tran.close()
      val results = adapter.engine.getResults().size
      assert(results == t.expectedResultSize)
    }
  )
}
