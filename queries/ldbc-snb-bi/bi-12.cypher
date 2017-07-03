// Trending Posts
MATCH
  (message:Message)-[:hasCreator]->(creator:Person),
  (message)<-[:likes]-(fan:Person)
WHERE message.creationDate > '2010-01-01T00:00:00.000+0000'
WITH message, creator, count(fan) AS likeCount
WHERE likeCount > 0
RETURN message.id, message.creationDate, creator.firstName, creator.lastName, likeCount
ORDER BY likeCount DESC, message.id ASC
LIMIT 100
