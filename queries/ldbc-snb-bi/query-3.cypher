MATCH (tag:Tag)<-[:HAS_TAG]-(message:Message)
WHERE message.creationDate < $year + $month
WITH tag, count(message) AS countMonth1
MATCH (tag)<-[:HAS_TAG]-(message:Message)
WHERE message.creationDate < $year + $month + 1
WITH tag, countMonth1, count(message) AS countMonth2
RETURN tag.name, countMonth1, countMonth2, abs(countMonth1-countMonth2) AS diff
ORDER BY diff desc, tag.name ASC
LIMIT 100