MATCH (n)
RETURN labels(n)[0] AS type, count(*) AS count, collect(n.host) AS names
