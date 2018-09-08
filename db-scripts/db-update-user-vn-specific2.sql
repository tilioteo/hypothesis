ALTER TABLE public.tbl_user ADD COLUMN gender character varying(1);

ALTER TABLE public.tbl_user ADD COLUMN education character varying(255);

ALTER TABLE public.tbl_user ADD COLUMN birth_date timestamp without time zone;

ALTER TABLE public.tbl_user ADD COLUMN testing_date timestamp without time zone;
