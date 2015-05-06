UPDATE tbl_event
SET xml_data=s.xml_data
FROM
(SELECT a.id event_id, '<?xml version="1.0" encoding="UTF-8"?>' || (xpath('/*', ('<EventData><Source Type="Slide" Id="' || a.slide_id  || '"/>' || array_to_string(xpath('/SlideData/*', b.xml_data::XML), '') || '<OutputValues>' || regexp_replace(regexp_replace(regexp_replace(array_to_string(xpath('/SlideOutput/Value', b.output::XML), ''), '<Value ', '<OutputValue Index="1" '), '</Value', '</OutputValue'), '<Value/>','') || '</OutputValues>' || '</EventData>')::XML)::TEXT[])[1] xml_data
FROM
(SELECT row_number() OVER (PARTITION BY t.test_id ORDER BY t.test_id, t.rank) AS num, t.test_id, e.* FROM tbl_test_event t, tbl_event e WHERE e.name='FINISH_SLIDE' AND t.event_id=e.id) a,
(SELECT row_number() OVER (PARTITION BY test_id ORDER BY test_id, id) AS num, * FROM tbl_slide_output) b
WHERE a.test_id=b.test_id AND a.slide_id=b.slide_id AND a.num=b.num) s
WHERE tbl_event.id=s.event_id;

DROP TABLE IF EXISTS "public"."tbl_slide_output" CASCADE;

ALTER TABLE "public"."tbl_event" ADD COLUMN "client_timestamp" BIGINT;