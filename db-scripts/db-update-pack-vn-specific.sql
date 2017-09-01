ALTER TABLE public.tbl_pack
   ADD COLUMN after_pack_id bigint;

ALTER TABLE public.tbl_pack
  ADD CONSTRAINT fk_after_pack FOREIGN KEY (after_pack_id) REFERENCES public.tbl_pack (id);