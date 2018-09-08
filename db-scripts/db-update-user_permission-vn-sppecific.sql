ALTER TABLE public.tbl_user_permission
   ADD COLUMN rank integer;

UPDATE public.tbl_user_permission SET rank=0;

ALTER TABLE public.tbl_user_permission
   ALTER COLUMN rank SET DEFAULT 0;

ALTER TABLE public.tbl_user_permission
   ALTER COLUMN rank SET NOT NULL;
