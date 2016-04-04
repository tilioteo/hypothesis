UPDATE tbl_slide
SET xml_data=s.xml_data
FROM
(SELECT id, REPLACE(xml_data, 'http://hypothesis.cz/xml/processing', 'http://hypothesis.cz/xml/procjs') xml_data FROM tbl_slide WHERE xml_data LIKE '%http://hypothesis.cz/xml/processing%') s
WHERE
tbl_slide.id=s.id;

UPDATE tbl_slide_template
SET xml_data=s.xml_data
FROM
(SELECT uid, REPLACE(xml_data, 'http://hypothesis.cz/xml/processing', 'http://hypothesis.cz/xml/procjs') xml_data FROM tbl_slide_template WHERE xml_data LIKE '%http://hypothesis.cz/xml/processing%') s
WHERE
tbl_slide_template.uid=s.uid;