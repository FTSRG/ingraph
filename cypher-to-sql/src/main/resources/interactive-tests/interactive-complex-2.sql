select p_personid, p_firstname, p_lastname, m_messageid, COALESCE(m_ps_imagefile,'')||COALESCE(m_content,''), m_creationdate
from person, message, knows_undirected
where
    p_personid = m_creatorid and
    m_creationdate <= to_bigint(TIMESTAMP 'epoch' + 1354060800000 * INTERVAL '1 ms') and
    k_person1id = 8796093022220 and
    k_person2id = p_personid
order by m_creationdate desc, m_messageid asc
limit 20
