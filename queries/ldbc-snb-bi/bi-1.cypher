// Q1. Posting summary
/*
  :param { date: 201009142200 }
*/
MATCH (message:Message)
WHERE message.creationDate <= $date
WITH toFloat(count(message)) AS totalMessageCount // this should be a subquery once Cypher supports it
MATCH (message:Message)
WHERE message.creationDate <= $date
  AND message.content IS NOT NULL
WITH
  totalMessageCount,
  message,
  message.creationDate/10000000000000 AS year,
  message.length AS length
WITH
  totalMessageCount,
  year,
  (message:Comment) AS isComment,
  CASE
    WHEN length <  40 THEN 0
    WHEN length <  80 THEN 1
    WHEN length < 160 THEN 2
    ELSE                   3
  END AS lengthCategory,
  count(message) AS messageCount,
  floor(avg(length)) AS averageMessageLength,
  sum(message.length) AS sumMessageLength
RETURN
  year,
  isComment,
  lengthCategory,
  messageCount,
  averageMessageLength,
  sumMessageLength,
  messageCount / totalMessageCount AS percentageOfMessages
ORDER BY
  year DESC,
  isComment ASC,
  lengthCategory ASC
