MATCH (person:Person)-[:KNOWS]-(:Person)<-[:HAS_CREATOR]-(post:Post)-[HAS_TAG]->(tag:Tag)
WHERE post.creationDate >= $date1
  AND post.creationDate < $date2
OPTIONAL MATCH (tag)<-[:HAS_TAG]-(oldPost:Post)
WHERE oldPost.creationDate < $date1
WITH tag, post, length(collect(oldPost)) AS oldPostCount
WHERE oldPostCount=0
RETURN
  tag.name AS tagName,
  length(collect(post)) AS postCount
ORDER BY
  postCount DESC,
  tagName ASC
LIMIT 10
