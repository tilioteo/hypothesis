UPDATE tbl_slide
SET xml_data=s.xml_data
FROM
(SELECT id, REPLACE(xml_data, 'ComponentData->getButtonIndex()', 'ComponentData->getSelectedIndex()') xml_data FROM tbl_slide WHERE xml_data LIKE '%ComponentData->getButtonIndex()%') s
WHERE
tbl_slide.id=s.id;

UPDATE tbl_slide_template
SET xml_data=s.xml_data
FROM
(SELECT uid, REPLACE(xml_data, 'ComponentData->getButtonIndex()', 'ComponentData->getSelectedIndex()') xml_data FROM tbl_slide_template WHERE xml_data LIKE '%ComponentData->getButtonIndex()%') s
WHERE
tbl_slide_template.uid=s.uid;