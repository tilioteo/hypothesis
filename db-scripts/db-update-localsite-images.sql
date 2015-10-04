UPDATE tbl_slide
SET xml_data=s.xml_data
FROM
(SELECT id, REPLACE(xml_data, 'http://hypothesis.cz/gallery/', 'http://192.168.53.1/~kamil/') xml_data FROM tbl_slide WHERE xml_data LIKE '%http://hypothesis.cz/gallery/%') s
WHERE
tbl_slide.id=s.id;