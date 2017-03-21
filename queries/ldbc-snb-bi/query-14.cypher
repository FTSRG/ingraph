MATCH (person:Person)<-[:hasCreator]-(message:Message)<-[:replyOf*]-(reply:Message)
WHERE "2010-01-01T00:00:00.000+0000" <= message.creationDate <= "2011-01-01T00:00:00.000+0000"
  AND "2010-01-01T00:00:00.000+0000" <= reply.creationDate   <= "2011-01-01T00:00:00.000+0000"
RETURN person.id, person.firstName, person.lastName, person.creationDate, count(message) AS threadCount, count(reply) AS messageCount
ORDER BY messageCount DESC, person.id ASC
LIMIT 100
