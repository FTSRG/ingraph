package ingraph.compiler.sql

import java.sql.{Connection, DriverManager}

import ingraph.compiler.sql.Util._
import ingraph.compiler.sql.driver.{SqlDriver, ValueJsonConversion}
import ingraph.driver.{CypherDriver, CypherDriverFactory}
import org.apache.log4j.{Level, LogManager}
import org.neo4j.driver.internal.InternalEntity
import org.neo4j.driver.internal.value._
import org.neo4j.driver.v1.{Session, Value}
import org.postgresql.jdbc.PgArray
import org.postgresql.util.PGobject
import org.scalactic.source
import org.scalatest._
import org.scalatest.exceptions.TestFailedException

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

trait Neo4jConnection extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  val driver: CypherDriver = CypherDriverFactory.createNeo4jDriver()
  val cypherSession: Session = driver.session()

  override def beforeEach(): Unit = {
    cypherSession.run("MATCH (n) DETACH DELETE n")

    super.beforeEach()
  }

  override def afterAll(): Unit = {
    try super.afterAll()
    finally {
      cypherSession.close()
      driver.close()
    }
  }
}

trait PostgresConnection extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  LogManager.getRootLogger.setLevel(Level.OFF)

  val postgres = new EmbeddedPostgresWrapper
  val sqlConnection: Connection = DriverManager.getConnection(postgres.Url)

  override def beforeEach(): Unit = {
    withResources(sqlConnection.createStatement)(_.execute(SqlQueries.purge))

    super.beforeEach()
  }

  override def afterAll(): Unit = {
    try super.afterAll()
    finally {
      postgres.close()
      sqlConnection.close()
    }
  }
}

class CompileSqlTest extends FunSuite with Neo4jConnection with PostgresConnection {

  private def compileAndRunQuery(createCypherQuery: String, selectCypherQuery: String, orderedResults: Boolean = false): Unit = {
    println("Create query:")
    println(createCypherQuery)
    println()

    val selectSqlQuery = SqlCompiler(selectCypherQuery).sql

    runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQuery, orderedResults)
  }

  def convertCypherCell(value: Any): Any = {
    value match {
      case value: IntegerValue => value.asLong()
      case value: NodeValue => value.asNode().id()
      case value: InternalEntity => value.id()
      case value: RelationshipValue => value.asRelationship().id()
      case value: StringValue => value.asString()
      case value: ListValue => value.asList.asScala.map(convertCypherCell)
      case value: NullValue => null
      case _ => value
    }
  }

  def convertSqlCell(value: Any): Any = {
    value match {
      case value: PGobject if value.getType == "jsonb" => {

        val cypherValue = ValueJsonConversion.gson.fromJson(value.getValue, classOf[Value])

        cypherValue match {
          case entity: EntityValueAdapter[_] => entity.asEntity().id()
          case _ => cypherValue.asObject()
        }
      }
      case value: PgArray => {
        value.getArray.asInstanceOf[Array[_]]
          .map { element =>
            if (value.getBaseTypeName == "jsonb") {
              val obj = new PGobject()
              obj.setType(value.getBaseTypeName)
              obj.setValue(element.asInstanceOf[String])

              obj
            }
            else
              element
          }
          .map(convertSqlCell)
      }
      case default => default
    }
  }

  def compareQueryResults(selectCypherQuery: String, sqlConnection: Connection, selectSqlQuery: String, orderedResults: Boolean): Unit = {
    withResources(sqlConnection.createStatement())(sqlStatement =>
      withResources(sqlStatement.executeQuery(selectSqlQuery))(sqlResultSet => {
        val cypherResult = cypherSession.run(selectCypherQuery)

        val cypherColumnNames = cypherResult.keys.asScala.toSeq
        val sqlColumnNames = (1 to sqlResultSet.getMetaData.getColumnCount).map(sqlResultSet.getMetaData.getColumnName(_)).toSeq
        assertResult(cypherColumnNames)(sqlColumnNames)

        val cypherResultList = cypherResult.asScala.map(record => record.values().asScala.toArray).toArray

        val sqlResultBuffer = new ListBuffer[Array[Any]]()
        while (sqlResultSet.next()) {
          sqlResultBuffer += (1 to sqlResultSet.getMetaData.getColumnCount).map(i => sqlResultSet.getObject(i)).toArray
        }
        val sqlResultList = sqlResultBuffer.toArray

        assertResult(cypherResultList.length)(sqlResultList.length)

        if (orderedResults) {
          for (rowIndex <- cypherResultList.indices) {
            val cypherRow = cypherResultList(rowIndex)
            val sqlRow = sqlResultList(rowIndex)

            assertResult(cypherRow.length)(sqlRow.length)
            for (columnIndex <- cypherRow.indices) {
              val cypherCell = convertCypherCell(cypherRow(columnIndex))
              val sqlCell = convertSqlCell(sqlRow(columnIndex))

              assertResult(cypherCell)(sqlCell)
            }
          }
        }
        else {
          val cypherResultSet = cypherResultList.map(row => row.map(convertCypherCell).deep).toSet
          val sqlResultSet = sqlResultList.map(row => row.map(convertSqlCell).deep).toSet
          assert(cypherResultSet == sqlResultSet)
        }
      }))
  }

  private def runGraphQuery(createCypherQuery: String, selectCypherQuery: String, selectSqlQuery: String, orderedResults: Boolean): Unit = {
    if (createCypherQuery.nonEmpty)
      cypherSession.run(createCypherQuery)

    println("vvvvvvvv REFERENCE RESULT vvvvvvvv")
    val result = cypherSession.run(selectCypherQuery)
    for (record <- result.asScala) {
      for ((key, value) <- record.asMap().asScala) {
        println(s"""$key = $value""")
      }
      println("---")
    }
    println("----------------------------------")

    withResources(sqlConnection.createStatement)(sqlStatement => {
      sqlStatement.executeUpdate(SqlQueries.createTables)
      sqlStatement.executeUpdate(SqlQueries.utilityFunctions)

      ExportSteps.execute(cypherSession, sqlConnection)

      SqlDriver.dump(sqlStatement, selectSqlQuery)

      compareQueryResults(selectCypherQuery, sqlConnection, selectSqlQuery, orderedResults)
    })
  }

  override def ignore(testName: String, testTags: Tag*)(testFun: => Any /* Assertion */)(implicit pos: source.Position): Unit = {
    super.ignore(testName, testTags: _*)(testFun)(pos)
  }

  override def test(testName: String, testTags: Tag*)(testFun: => Any /* Assertion */)(implicit pos: source.Position): Unit = {
    super.test(testName, testTags: _*) {
      println(s"vvvvvvvvvvvvvvvv $testName vvvvvvvvvvvvvvvv")
      println()

      testFun
    }(pos)
  }

  private def testsForComparison(): Unit = {
    val createCypherQuery = "CREATE ({value: 1}), ({value: 2}), ({value: 3})"
    val selectCypherQuery =
      """MATCH (n)
        |RETURN n.value AS val ORDER BY val""".stripMargin

    val columnNamePrefix =
      """SELECT NULL AS val
        |  WHERE FALSE
        |UNION ALL
        |""".stripMargin
    val selectSqlQuery = columnNamePrefix +
      """VALUES (1),
        |  (2),
        |  (3)""".stripMargin
    val selectSqlQueryFaulty = columnNamePrefix +
      """VALUES (1),
        |  (2),
        |  (0)""".stripMargin
    val selectSqlQueryShorter = columnNamePrefix +
      """VALUES (1),
        |  (2)""".stripMargin
    val selectSqlQueryReversed = columnNamePrefix +
      """VALUES (3),
        |  (2),
        |  (1)""".stripMargin

    var testNamePrefix = "Comparison - ordered: "

    test(testNamePrefix + "same") {
      runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQuery, true)
    }

    test(testNamePrefix + "reverse order") {
      assertThrows[TestFailedException] {
        runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQueryReversed, true)
      }
    }

    test(testNamePrefix + "shorter") {
      assertThrows[TestFailedException] {
        runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQueryShorter, true)
      }
    }

    testNamePrefix = "Comparison - unordered: "

    test(testNamePrefix + "reverse order") {
      runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQueryReversed, false)
    }

    test(testNamePrefix + "different element") {
      assertThrows[TestFailedException] {
        runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQueryFaulty, false)
      }
    }

    test(testNamePrefix + "shorter") {
      assertThrows[TestFailedException] {
        runGraphQuery(createCypherQuery, selectCypherQuery, selectSqlQueryShorter, false)
      }
    }
  }

  testsForComparison()

  ignore("Create, filter, return different types") {
    compileAndRunQuery("CREATE ({str: 'abc', int: 1, float: 1.1, bool: false}), ({str: 'xyz', int: 2, float: 2.1, bool: true})",
      """MATCH (n)
        |WHERE n.int >= 2
        |RETURN n.str, n.int, n.float, n.bool, 1 AS value, 1+1 AS sum""".stripMargin)
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L52
  test("Use multiple MATCH clauses to do a Cartesian product") {
    compileAndRunQuery("CREATE ({value: 1}), ({value: 2}), ({value: 3})",
      """MATCH (n), (m)
        |RETURN n.value AS n, m.value AS m""".stripMargin)
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L97
  test("Filter out based on node prop name") {
    compileAndRunQuery("CREATE ({name: 'Someone'})<-[:X]-()-[:X]->({name: 'Andres'})",
      """MATCH ()-[rel:X]-(a)
        |WHERE a.name = 'Andres'
        |RETURN a""".stripMargin)
  }

  test("Filter out based on node prop name / fragment #1") {
    compileAndRunQuery(
      """CREATE ()""",
      """MATCH (a)
        |WHERE a.name = 'x'
        |RETURN a""".stripMargin
    )
  }

  test("Filter out based on node prop name / fragment #2") {
    compileAndRunQuery(
      """CREATE ({name: 'Someone'})<-[:X]-()-[:X]->({name: 'Andres'})""",
      """MATCH (a)
        |RETURN a""".stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L131
  test("Filter based on rel prop name") {
    compileAndRunQuery(
      """CREATE (:A)<-[:KNOWS {name: 'monkey'}]-()-[:KNOWS {name: 'woot'}]->(:B)""",
      """MATCH (node)-[r:KNOWS]->(a)
        |WHERE r.name = 'monkey'
        |RETURN a
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L148
  test("Cope with shadowed variables") {
    compileAndRunQuery(
      """
        |CREATE ({value: 1, name: 'King Kong'}),
        |  ({value: 2, name: 'Ann Darrow'})
      """.stripMargin,
      """MATCH (n)
        |WITH n.name AS n
        |RETURN n
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L167
  test("Get neighbours") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})""",
      """MATCH (n1)-[rel:KNOWS]->(n2)
        |RETURN n1, n2
      """.stripMargin
    )
  }

  test("Get neighbours / plus edges with different type, multiple possible types in query") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND_OF]->(c:C {value: 3}), (:V)-[:OTHER]->(:V)""",
      """MATCH (n1)-[rel:KNOWS|FRIEND_OF]->(n2)
        |RETURN n1, n2
      """.stripMargin
    )
  }

  test("Get neighbours / undirected edge with type constraint") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND_OF]->(c:C {value: 3}), (:V)-[:OTHER]->(:V)""",
      """MATCH (n1)-[rel:KNOWS]-(n2)
        |RETURN n1, n2
      """.stripMargin
    )
  }

  test("Get neighbours / without edge type constraint") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND_OF]->(c:C {value: 3}), (:V)-[:OTHER]->(:V)""",
      """MATCH (n1)-[rel]->(n2)
        |RETURN n1, n2
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L183
  test("Get two related nodes") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1}),
        |  (a)-[:KNOWS]->(b:B {value: 2}),
        |  (a)-[:KNOWS]->(c:C {value: 3})
      """.stripMargin,
      """MATCH ()-[rel:KNOWS]->(x)
        |RETURN x
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L202
  test("Get related to related to / untyped") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND]->(c:C {value: 3})""",
      """MATCH (n)-->(a)-->(b)
        |RETURN b
      """.stripMargin
    )
  }

  test("Get related to related to / undirected") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND]->(c:C {value: 3})""",
      """MATCH (n)--(a)--(b)
        |RETURN b
      """.stripMargin
    )
  }

  test("Get related to related to / more edges") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND]->(c:C {value: 3}), (b)-[:FRIEND]->(d:D {value: 4})""",
      """MATCH (n)-->(a)-->(b), (a)-->(d)
        |RETURN b
      """.stripMargin
    )
  }

  test("Get related to related to / typed") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:KNOWS]->(b:B {value: 2})-[:FRIEND]->(c:C {value: 3})""",
      """MATCH (n)-[:KNOWS]->(a)-[:FRIEND]->(b)
        |RETURN b
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L218
  test("Handle comparison between node properties") {
    compileAndRunQuery(
      """CREATE (a:A {animal: 'monkey'}),
        |  (b:B {animal: 'cow'}),
        |  (c:C {animal: 'monkey'}),
        |  (d:D {animal: 'cow'}),
        |  (a)-[:KNOWS]->(b),
        |  (a)-[:KNOWS]->(c),
        |  (d)-[:KNOWS]->(b),
        |  (d)-[:KNOWS]->(c)
      """.stripMargin,
      """MATCH (n)-[rel:KNOWS]->(x)
        |WHERE n.animal = x.animal
        |RETURN n, x
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L243
  test("Return two subgraphs with bound undirected relationship") {
    compileAndRunQuery(
      """CREATE (a:A {value: 1})-[:REL {name: 'r'}]->(b:B {value: 2})
      """.stripMargin,
      """MATCH (a)-[r:REL {name: 'r'}]-(b)
        |RETURN a, b
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance.feature#L323
  test("Handle OR in the WHERE clause") {
    compileAndRunQuery(
      """CREATE (a:A {p1: 12}),
        |  (b:B {p2: 13}),
        |  (c:C)
      """.stripMargin,
      """MATCH (n)
        |WHERE n.p1 = 12 OR n.p2 = 13
        |RETURN n
      """.stripMargin
    )
  }

  // MatchAcceptance2.feature

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L20
  test("Do not return non-existent nodes") {
    compileAndRunQuery(
      "",
      """MATCH (n)
        |RETURN n
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L31
  test("Do not return non-existent relationships") {
    compileAndRunQuery(
      "",
      """MATCH ()-[r:LOLZ]->()
        |RETURN r
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L129
  test("Simple variable length pattern") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (c {name: 'C'}), (d {name: 'D'})
        |CREATE (a)-[:CONTAINS]->(b),
        |       (b)-[:CONTAINS]->(c),
        |       (c)-[:CONTAINS]->(d)
      """.stripMargin,
      """MATCH (a {name: 'A'})-[:CONTAINS*]->(x)
        |RETURN x
      """.stripMargin
    )
  }

  test("Simple variable length pattern / undirected edge") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (c {name: 'C'}), (d {name: 'D'})
        |CREATE (a)-[:CONTAINS]->(b),
        |       (b)-[:CONTAINS]->(c),
        |       (c)-[:CONTAINS]->(d)
      """.stripMargin,
      """MATCH (a {name: 'A'})-[:CONTAINS*]-(x)
        |RETURN x
      """.stripMargin
    )
  }

  test("Simple variable length pattern / exact length") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (c {name: 'C'}), (d {name: 'D'})
        |CREATE (a)-[:CONTAINS]->(b),
        |       (b)-[:CONTAINS]->(c),
        |       (c)-[:CONTAINS]->(d)
      """.stripMargin,
      """MATCH (a {name: 'A'})-[:CONTAINS*2]->(x)
        |RETURN x
      """.stripMargin
    )
  }

  test("Simple variable length pattern / same upper and lower bound") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (c {name: 'C'}), (d {name: 'D'})
        |CREATE (a)-[:CONTAINS]->(b),
        |       (b)-[:CONTAINS]->(c),
        |       (c)-[:CONTAINS]->(d)
      """.stripMargin,
      """MATCH (a {name: 'A'})-[:CONTAINS*2..2]->(x)
        |RETURN x
      """.stripMargin
    )
  }

  test("Simple variable length pattern / zero lower bound") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (c {name: 'C'}), (d {name: 'D'})
        |CREATE (a)-[:CONTAINS]->(b),
        |       (b)-[:CONTAINS]->(c),
        |       (c)-[:CONTAINS]->(d)
      """.stripMargin,
      """MATCH (a {name: 'A'})-[:CONTAINS*0..]->(x)
        |RETURN x
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L191
  test("Returning bound nodes that are not part of the pattern") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (c {name: 'C'})
        |CREATE (a)-[:KNOWS]->(b)
      """.stripMargin,
      """MATCH (a {name: 'A'}), (c {name: 'C'})
        |MATCH (a)-[:KNOWS]->(b)
        |RETURN a, b, c
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L210
  test("Two bound nodes pointing to the same node") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}),
        |       (x1 {name: 'x1'}), (x2 {name: 'x2'})
        |CREATE (a)-[:KNOWS]->(x1),
        |       (a)-[:KNOWS]->(x2),
        |       (b)-[:KNOWS]->(x1),
        |       (b)-[:KNOWS]->(x2)
      """.stripMargin,
      """MATCH (a {name: 'A'}), (b {name: 'B'})
        |MATCH (a)-[:KNOWS]->(x)<-[:KNOWS]->(b)
        |RETURN x
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L233
  test("Three bound nodes pointing to the same node") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}), (b {name: 'B'}), (c {name: 'C'}),
        |       (x1 {name: 'x1'}), (x2 {name: 'x2'})
        |CREATE (a)-[:KNOWS]->(x1),
        |       (a)-[:KNOWS]->(x2),
        |       (b)-[:KNOWS]->(x1),
        |       (b)-[:KNOWS]->(x2),
        |       (c)-[:KNOWS]->(x1),
        |       (c)-[:KNOWS]->(x2)
      """.stripMargin,
      """MATCH (a {name: 'A'}), (b {name: 'B'}), (c {name: 'C'})
        |MATCH (a)-[:KNOWS]->(x), (b)-[:KNOWS]->(x), (c)-[:KNOWS]->(x)
        |RETURN x
      """.stripMargin
    )
  }

  // add more MATCH tests later
  // from https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/MatchAcceptance2.feature#L258

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L38
  test("ORDER BY and LIMIT can be used - edited") {
    compileAndRunQuery(
      """CREATE (a:A {name: 'X'}),
        |(a)-[:REL]->()
      """.stripMargin,
      """MATCH (a:A)
        |WITH a
        |ORDER BY a.name
        |LIMIT 1
        |MATCH (a)-[:REL]->(b)
        |RETURN a
      """.stripMargin
    )
  }

  test("ORDER BY and LIMIT can be used - edited, with negative examples") {
    compileAndRunQuery(
      """CREATE (:A {name: 'X'})-[:REL]->(), (:A {name: 'A'})-[:REL]->(), (:A {name: 'Y'})-[:REL]->()
      """.stripMargin,
      """MATCH (a:A)
        |WITH a
        |ORDER BY a.name
        |LIMIT 1
        |MATCH (a)-[:REL]->(b)
        |RETURN a
      """.stripMargin
    )
  }

  ignore("ORDER BY and LIMIT can be used - get first element and computation in WITH and RETURN") {
    compileAndRunQuery(
      """CREATE ({name: 'c'}), ({name: 'd'}), ({name: 'a'}), ({name: 'b'})
      """.stripMargin,
      """MATCH (a)
        |WITH a, a.name AS name, (1 + 1) AS sum2
        |ORDER BY a.name
        |LIMIT 1
        |RETURN a, name, sum2, (1 + 2) AS sum3
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L59
  test("No dependencies between the query parts") {
    compileAndRunQuery(
      """CREATE (:A), (:B)
      """.stripMargin,
      """|MATCH (a)
         |WITH a
         |MATCH (b)
         |RETURN a, b
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L80
  test("Aliasing") {
    compileAndRunQuery(
      """CREATE (:Begin {prop: 42}),
        |       (:End {prop: 42}),
        |       (:End {prop: 3})
      """.stripMargin,
      """MATCH (a:Begin)
        |WITH a.prop AS property
        |MATCH (b:End)
        |WHERE property = b.prop
        |RETURN b
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L101
  test("Handle dependencies across WITH - CREATE edited") {
    compileAndRunQuery(
      """CREATE (a:End {prop: 42, id: 0}),
        |       (:End {prop: 3}),
        |       (:Begin {prop: 0})
      """.stripMargin,
      """MATCH (a:Begin)
        |WITH a.prop AS property
        |  ORDER BY property
        |  LIMIT 1
        |MATCH (b)
        |WHERE b.id = property
        |RETURN b
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L123
  test("Handle dependencies across WITH with SKIP - CREATE edited") {
    compileAndRunQuery(
      """CREATE ({prop: 'A', key: 0, id: 0}),
        |       ({prop: 'B', key: 0, id: 1}),
        |       ({prop: 'C', key: 0, id: 2})
      """.stripMargin,
      """MATCH (a)
        |WITH a.prop AS property, a.key AS idToUse
        |  ORDER BY property
        |  SKIP 1
        |MATCH (b)
        |WHERE b.id = idToUse
        |RETURN DISTINCT b
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L146
  test("WHERE after WITH should filter results") {
    compileAndRunQuery(
      """CREATE ({name: 'A'}),
        |       ({name: 'B'}),
        |       ({name: 'C'})
      """.stripMargin,
      """MATCH (a)
        |WITH a
        |WHERE a.name = 'B'
        |RETURN a
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L166
  test("WHERE after WITH can filter on top of an aggregation - edited") {
    compileAndRunQuery(
      """CREATE (a {name: 'A'}),
        |       (b {name: 'B'})
        |CREATE (a)-[:REL]->(),
        |       (a)-[:REL]->(),
        |       (a)-[:REL]->(),
        |       (b)-[:REL]->()
      """.stripMargin,
      """MATCH (a)-[:REL]->(b)
        |WITH a, count(b) AS relCount
        |WHERE relCount > 1
        |RETURN a
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/WithAcceptance.feature#L251
  test("A simple pattern with one bound endpoint - edited") {
    compileAndRunQuery(
      """CREATE (:A {x: 'x1'})-[:REL]->(:B {x: 'x2'})""",
      """MATCH (a:A)-[r:REL]->(b:B)
        |WITH a AS b, b AS tmp, r AS r
        |WITH b AS a, r
        |ORDER BY a.x, b.x
        |LIMIT 1
        |MATCH (a)-[r:REL]->(b)
        |RETURN a, r, b
      """.stripMargin
    )
  }

  test("Simple collect") {
    compileAndRunQuery(
      """CREATE (), ()
      """.stripMargin,
      """MATCH (n)
        |RETURN collect(n) AS ns
      """.stripMargin
    )
  }

  test("Property collect") {
    compileAndRunQuery(
      """CREATE ({p: 1}), ({p: 2})
      """.stripMargin,
      """MATCH (n)
        |RETURN collect(n.p) AS ns
      """.stripMargin
    )
  }

  test("Property collect on edge") {
    compileAndRunQuery(
      """CREATE
        |  (x:X {id: 99}),
        |  (x)-[:REL]->({p: 1}),
        |  (x)-[:REL]->({p: 2})
      """.stripMargin,
      """MATCH (x:X)-[:REL]->(n)
        |RETURN x.id, collect(n.p) AS ns
      """.stripMargin
    )
  }

  ignore("List selectors") {
    compileAndRunQuery(
      """CREATE (:X { list: ['a', 'b'] })
      """.stripMargin,
      """MATCH (x:X)
        |RETURN x.list[1]
      """.stripMargin
    )
  }

  test("List selectors / with column name") {
    compileAndRunQuery(
      """CREATE (:X { list: ['a', 'b'] })
      """.stripMargin,
      """MATCH (x:X)
        |RETURN x.list[1] AS element
      """.stripMargin
    )
  }

  // https://github.com/opencypher/openCypher/blob/5a2b8cc8037225b4158e231e807a678f90d5aa1d/tck/features/OptionalMatchAcceptance.feature#L57
  test("Respect predicates on the OPTIONAL MATCH") {
    compileAndRunQuery(
      """CREATE (s:Single), (a:A {prop: 42}),
        |       (b:B {prop: 46}), (c:C)
        |CREATE (s)-[:REL]->(a),
        |       (s)-[:REL]->(b),
        |       (a)-[:REL]->(c),
        |       (b)-[:LOOP]->(b)""".stripMargin,
      """MATCH (n:Single)
        |OPTIONAL MATCH (n)-[r:REL]-(m)
        |WHERE m.prop = 42
        |RETURN m
      """.stripMargin
    )
  }
  test("Respect predicates on the OPTIONAL MATCH / empty optional result") {
    compileAndRunQuery(
      """CREATE (s:Single)""",
      """MATCH (n:Single)
        |OPTIONAL MATCH (n)-[r:REL]-(m)
        |WHERE m.prop = 42
        |RETURN m
      """.stripMargin
    )
  }
  test("Respect predicates on the OPTIONAL MATCH / condition about existing and null vertices") {
    compileAndRunQuery(
      """CREATE (:Single {num: 1}),
        |       (:Single {num: 2}),
        |       (:Single {num: 3})-[:REL]->({prop: 42}),
        |       (:Single {num: 1})-[:REL]->({prop: 42}),
        |       (:Single {num: 1})-[:REL]->()
      """.stripMargin,
      """MATCH (n:Single)
        |OPTIONAL MATCH (n)-[r:REL]-(m)
        |WHERE m.prop = 42 AND n.num <> 1
        |RETURN n, n.num, m, m.prop
      """.stripMargin
    )
  }

  // custom aggregation test
  test("Count DISTINCT") {
    compileAndRunQuery(
      """CREATE
        | (p1:Person),
        | (p2:Person),
        | (c:City),
        | (p1)-[:LIVES_IN]->(c),
        | (p2)-[:LIVES_IN]->(c)
      """.stripMargin,
      """MATCH (p:Person)-[:LIVES_IN]->(c:City)
        |RETURN count(DISTINCT c) AS cc
      """.stripMargin
    )
  }

  // custom aggregation test
  test("Count DISTINCT properties") {
    compileAndRunQuery(
      """CREATE
        | (p1:Person {name: 'Alan'}),
        | (p2:Person {name: 'Alan'}),
        | (c:City),
        | (p1)-[:LIVES_IN]->(c),
        | (p2)-[:LIVES_IN]->(c)
      """.stripMargin,
      """MATCH (p:Person)-[:LIVES_IN]->(c:City)
        |RETURN count(DISTINCT p.name) AS ps
      """.stripMargin
    )
  }

  test("Show null on OPTIONAL MATCH") {
    compileAndRunQuery("",
      """OPTIONAL MATCH (n)
        |RETURN n
      """.stripMargin
    )
  }

  test("count empty on OPTIONAL MATCH") {
    compileAndRunQuery("",
      """OPTIONAL MATCH (n)
        |RETURN count(n) AS cn
      """.stripMargin
    )
  }

  test("count empty on MATCH") {
    compileAndRunQuery("",
      """MATCH (n)
        |RETURN count(n) AS cn
      """.stripMargin
    )
  }

  test("Filter out based on node label") {
    compileAndRunQuery("CREATE (:A {labels: 'A'}), (:A:B {labels: 'A-B'}), (:B {labels: 'B'}), (:C {labels: 'C'}), ({labels: ''})",
      """MATCH (a:A)
        |RETURN a, a.labels""".stripMargin)
  }

  test("Filter out based on more node labels") {
    compileAndRunQuery("CREATE (:A {labels: 'A'}), (:A:B {labels: 'A-B'}), (:B {labels: 'B'}), (:C {labels: 'C'}), ({labels: ''})",
      """MATCH (a:A:B)
        |RETURN a, a.labels""".stripMargin)
  }

  test("Filter out based on node label with duplicate") {
    compileAndRunQuery("CREATE (:A {labels: 'A'}), (:A:B {labels: 'A-B'}), (:B {labels: 'B'}), (:C {labels: 'C'}), ({labels: ''})",
      """MATCH (a:A:A)
        |RETURN a, a.labels""".stripMargin)
  }

  test("Filter out based on node label in WHERE clause") {
    compileAndRunQuery("CREATE (:A {labels: 'A'}), (:A:B {labels: 'A-B'}), (:B {labels: 'B'}), (:C {labels: 'C'}), ({labels: ''})",
      """MATCH (a)
        |WHERE a:A:B OR a:C
        |RETURN a, a.labels""".stripMargin)
  }

  test("Filter out based on WHERE NOT clause") {
    compileAndRunQuery("CREATE (:A {label: 'ok'}), (:A {label: 'nok'})-[:CONNECTED]->(:Exclude)",
      """MATCH (n:A)
        |WHERE NOT (n)-[]-(:Exclude)
        |RETURN n""".stripMargin)
  }

  test("Filter out based on vertex label constraint on both vertices of GetEdges") {
    compileAndRunQuery(
      """CREATE (:A)-[:E {id: 1}]->(:Exclude),
        |       (:A)-[:E {id: 2}]->(:Include),
        |       (:B)-[:E {id: 3}]->(:Include)
      """.stripMargin,
      """MATCH (:A)-[e:E]-(:Include)
        |RETURN e
      """.stripMargin)
  }

  test("Filter out based on vertex label constraint on one vertex of GetEdges") {
    compileAndRunQuery(
      """CREATE (:A)-[:E {id: 1}]->(:Exclude),
        |       (:A)-[:E {id: 2}]->(:Include),
        |       (:B)-[:E {id: 3}]->(:Include)
      """.stripMargin,
      """MATCH ()-[e:E]-(:Include)
        |RETURN e
      """.stripMargin)
  }
}
