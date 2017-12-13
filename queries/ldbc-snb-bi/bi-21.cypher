// Q21. Zombies in a country
/*
  :param {
    country: 'Spain',
    endDate: 20130101050000000
  }
*/
MATCH (country:Country {name: $country})
WITH
  country,
  $endDate/10000000000000   AS endDateYear,
  $endDate/100000000000%100 AS endDateMonth
MATCH
  (country)<-[:IS_PART_OF]-(:City)<-[:IS_LOCATED_IN]-
  (person:Person)<-[:HAS_CREATOR]-(message:Message)
WHERE person.creationDate < $endDate
  AND message.creationDate < $endDate
WITH
  country,
  person,
  endDateYear,
  endDateMonth,
  message.creationDate/10000000000000   AS personCreationYear,
  message.creationDate/100000000000%100 AS personCreationMonth,
  count(message) AS messageCount
WITH
  country,
  person,
  (endDateYear  - personCreationYear ) * 12 +
  (endDateMonth - personCreationMonth) AS months,
  messageCount
WHERE messageCount / months < 1
WITH
  country,
  collect(person) AS zombies
MATCH
  (country)<-[:IS_PART_OF]-(:City)<-[:IS_LOCATED_IN]-
  (person:Person)<-[:HAS_CREATOR]-(message:Message)<-[:LIKES]-(fan:Person)
WHERE fan.creationDate < $endDate
WITH
  zombies,
  person,
  collect(fan) AS fans
WITH
  person,
  size([f IN fans WHERE f in zombies]) AS zombieLikeCount,
  toFloat(size(fans)) AS totalLikeCount
RETURN
  person.id,
  zombieLikeCount,
  totalLikeCount,
  zombieLikeCount / totalLikeCount AS zombieScore
ORDER BY
  zombieScore DESC,
  person.id ASC
LIMIT 100
