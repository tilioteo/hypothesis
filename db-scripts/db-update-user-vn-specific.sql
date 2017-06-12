ALTER TABLE public.tbl_user
  DROP CONSTRAINT tbl_user_username_key;

ALTER TABLE public.tbl_user
  ADD CONSTRAINT tbl_user_username_password_key UNIQUE(username, password);

ALTER TABLE public.tbl_user ADD COLUMN name character varying(255);
