--
-- PostgreSQL database dump
--


-- Sequence: public.hbn_score_seq

CREATE SEQUENCE hbn_score_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE hbn_score_seq
  OWNER TO hypothesis;

--
-- TOC entry 218 (class 1259 OID 20011)
-- Name: tbl_score; Type: TABLE; Schema: public; Owner: hypothesis
--

CREATE TABLE tbl_score (
    id bigint NOT NULL,
    xml_data text,
    name character varying(255),
    "timestamp" bigint NOT NULL,
    branch_id bigint,
    slide_id bigint,
    task_id bigint
);


ALTER TABLE tbl_score OWNER TO hypothesis;

--
-- TOC entry 219 (class 1259 OID 20019)
-- Name: tbl_test_score; Type: TABLE; Schema: public; Owner: hypothesis
--

CREATE TABLE tbl_test_score (
    test_id bigint,
    score_id bigint NOT NULL,
    rank integer NOT NULL
);


ALTER TABLE tbl_test_score OWNER TO hypothesis;

--
-- TOC entry 2094 (class 2606 OID 20018)
-- Name: tbl_score_pkey; Type: CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_score
    ADD CONSTRAINT tbl_score_pkey PRIMARY KEY (id);


--
-- TOC entry 2096 (class 2606 OID 20023)
-- Name: tbl_test_score_pkey; Type: CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_test_score
    ADD CONSTRAINT tbl_test_score_pkey PRIMARY KEY (score_id);


--
-- TOC entry 2098 (class 2606 OID 20054)
-- Name: uk_mk4vhtn5n4h8j2em9i6q37j72; Type: CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_test_score
    ADD CONSTRAINT uk_mk4vhtn5n4h8j2em9i6q37j72 UNIQUE (score_id);


--
-- TOC entry 2099 (class 2606 OID 20024)
-- Name: fk_2e5fvrvvp848264pw60ggeq3y; Type: FK CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_score
    ADD CONSTRAINT fk_2e5fvrvvp848264pw60ggeq3y FOREIGN KEY (branch_id) REFERENCES tbl_branch(id);


--
-- TOC entry 2101 (class 2606 OID 20034)
-- Name: fk_2oh07uj1i9abewdk9bnlrghar; Type: FK CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_score
    ADD CONSTRAINT fk_2oh07uj1i9abewdk9bnlrghar FOREIGN KEY (task_id) REFERENCES tbl_task(id);


--
-- TOC entry 2102 (class 2606 OID 20039)
-- Name: fk_7s7ugghvu0ekaspc52q5168e0; Type: FK CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_test_score
    ADD CONSTRAINT fk_7s7ugghvu0ekaspc52q5168e0 FOREIGN KEY (test_id) REFERENCES tbl_test(id);


--
-- TOC entry 2103 (class 2606 OID 20044)
-- Name: fk_mk4vhtn5n4h8j2em9i6q37j72; Type: FK CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_test_score
    ADD CONSTRAINT fk_mk4vhtn5n4h8j2em9i6q37j72 FOREIGN KEY (score_id) REFERENCES tbl_score(id);


--
-- TOC entry 2100 (class 2606 OID 20029)
-- Name: fk_qudw3to1xhuhjcxw0ix187ll4; Type: FK CONSTRAINT; Schema: public; Owner: hypothesis
--

ALTER TABLE ONLY tbl_score
    ADD CONSTRAINT fk_qudw3to1xhuhjcxw0ix187ll4 FOREIGN KEY (slide_id) REFERENCES tbl_slide(id);


-- Completed on 2017-02-10 00:14:45

--
-- PostgreSQL database dump complete
--

