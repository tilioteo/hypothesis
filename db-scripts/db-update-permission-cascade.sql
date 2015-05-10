ALTER TABLE "public"."tbl_user_permission" DROP CONSTRAINT IF EXISTS "fk284a813a216e3a1d";
ALTER TABLE "public"."tbl_user_permission" ADD CONSTRAINT "fk284a813a216e3a1d" FOREIGN KEY("user_id") REFERENCES "public"."tbl_user"("id") MATCH SIMPLE ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE "public"."tbl_group_permission" DROP CONSTRAINT IF EXISTS "fk3b80556c399f1577";
ALTER TABLE "public"."tbl_group_permission" ADD CONSTRAINT "fk3b80556c399f1577" FOREIGN KEY( "group_id") REFERENCES "public"."tbl_group"("id") MATCH SIMPLE ON DELETE CASCADE ON UPDATE NO ACTION;
