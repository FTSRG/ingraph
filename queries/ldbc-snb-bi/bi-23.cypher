// Holiday destinations
MATCH (homeCountry:Country)<-[:isPartOf]-(:City)<-[:isLocatedIn]-(:Person)<-[:hasCreator]-(message:Message)-[:isLocatedIn]->(country:Country)
WHERE homeCountry <> country
WITH message, country, toInt(substring(message.creationDate, 5, 2)) AS month
RETURN count(message) AS messageCount, country.name, month
ORDER BY messageCount DESC, country.name ASC, month DESC
LIMIT 100
