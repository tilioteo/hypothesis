ALTER TABLE "public"."tbl_slide" ADD COLUMN "slide_template_uid" CHARACTER VARYING( 255 );
ALTER TABLE "public"."tbl_slide" ADD COLUMN "xml_data" TEXT;

UPDATE tbl_slide
SET slide_template_uid=c.slide_template_uid,
	xml_data=c.xml_data
FROM (SELECT id, slide_template_uid, xml_data FROM tbl_slide_content) c
WHERE c.id=tbl_slide.slide_content_id;

UPDATE tbl_slide
SET note=c.note
FROM (SELECT id, note FROM tbl_slide_content) c
WHERE tbl_slide.note IS NULL AND c.id=tbl_slide.slide_content_id;

ALTER TABLE "public"."tbl_slide_content" DROP CONSTRAINT IF EXISTS "fkfa3ef4ca3a3d0f41";
ALTER TABLE "public"."tbl_slide" DROP CONSTRAINT IF EXISTS "fkc9ad3db0456670ba";
ALTER TABLE "public"."tbl_slide" ADD CONSTRAINT "fkfa3ef4ca3a3d0f41" FOREIGN KEY ( "slide_template_uid"	) REFERENCES "public"."tbl_slide_template" ( "uid" ) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."tbl_slide" DROP COLUMN "slide_content_id";
ALTER TABLE "public"."tbl_slide" ALTER COLUMN "slide_template_uid" SET NOT NULL;
ALTER TABLE "public"."tbl_slide" ALTER COLUMN "xml_data" SET NOT NULL;

DROP TABLE IF EXISTS "public"."tbl_slide_content" CASCADE;


ALTER TABLE "public"."tbl_pack" ADD COLUMN "java_required" BOOLEAN DEFAULT 'true' NOT NULL;
ALTER TABLE "public"."tbl_task" ADD COLUMN "xml_data" TEXT;
