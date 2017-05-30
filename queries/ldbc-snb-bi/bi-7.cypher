// Most authoritative users on a given topic
MATCH
  (tag:Tag)<-[:hasTag]-(:Message)-[:hasCreator]->(person1:Person)
MATCH
  (person)<-[:hasCreator]-(message:Message)-[:hasTag]->(tag),
  (message)<-[:likes]-(person2:Person)<-[:hasCreator]-(:Message)<-[:likes]-(person3:Person)
RETURN person1.id, count(person3) AS authorityScore
ORDER BY authorityScore DESC, person1.id ASC
LIMIT 100
