// Q2. Top tags for country, age, gender, time
/*
  :param {
    date1: 20091231230000000,
    date2: 20101107230000000,
    country1: 'Ethiopia',
    country2: 'Spain'
  }
*/
MATCH
  (country:Country)<-[:isPartOf]-(:City)<-[:isLocatedIn]-(person:Person)
  <-[:hasCreator]-(message:Message)-[:hasTag]->(tag:Tag)
WHERE message.creationDate >= $date1
  AND message.creationDate <= $date2
  AND (country.name = $country1 OR country.name = $country2)
WITH
  country.name AS countryName,
  message.creationDate/1000000%100 AS month,
  person.gender AS gender,
  floor((2013 - person.birthday/10000000000000) / 5.0) AS ageGroup,
  tag.name AS tagName,
  message
WITH
  countryName, month, gender, ageGroup, tagName, count(message) AS messageCount
WHERE messageCount > 100
RETURN
  countryName,
  month,
  gender,
  ageGroup,
  tagName,
  messageCount
ORDER BY
  messageCount DESC,
  tagName ASC,
  ageGroup ASC,
  gender ASC,
  month ASC,
  countryName ASC
LIMIT 100
