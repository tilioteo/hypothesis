CREATE TABLE public.tbl_pack_set
(
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  CONSTRAINT tbl_pack_set_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.tbl_pack_set
  OWNER TO hypothesis;

CREATE SEQUENCE public.hbn_pack_set_seq
  INCREMENT 1
  MINVALUE 1
  START 1
  CACHE 1;
ALTER TABLE public.hbn_pack_set_seq
  OWNER TO hypothesis;

CREATE TABLE public.tbl_pack_set_pack
(
  pack_set_id bigint NOT NULL,
  pack_id bigint NOT NULL,
  rank integer NOT NULL,
  CONSTRAINT tbl_pack_set_pack_pkey PRIMARY KEY (pack_set_id, rank),
  CONSTRAINT fk_pack_id FOREIGN KEY (pack_id)
      REFERENCES public.tbl_pack (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_pack_set_id FOREIGN KEY (pack_set_id)
      REFERENCES public.tbl_pack_set (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.tbl_pack_set_pack
  OWNER TO hypothesis;
