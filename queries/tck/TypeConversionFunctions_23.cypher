UNWIND ['male', 'female', null] AS gen
RETURN coalesce(toString(gen), 'x') AS result
