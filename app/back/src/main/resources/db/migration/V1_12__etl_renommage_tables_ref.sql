--ALTER TABLE ref_critere_analyse_candidature RENAME TO ref_critere_analyse_candidature;
--ALTER TABLE formation RENAME TO ref_formation;
--ALTER TABLE moyenne_generale_admis RENAME TO ref_moyenne_generale_admis;
--ALTER TABLE triplet_affectation RENAME TO ref_triplet_affectation;
--ALTER TABLE metier RENAME TO ref_metier;
--ALTER TABLE baccalaureat RENAME TO ref_baccalaureat;
--ALTER TABLE join_baccalaureat_specialite RENAME TO ref_join_baccalaureat_specialite;
--ALTER TABLE domaine_categorie RENAME TO ref_domaine_categorie;
--ALTER TABLE domaine RENAME TO ref_domaine;
--ALTER TABLE interet_categorie RENAME TO ref_interet_categorie;
--ALTER TABLE interet RENAME TO ref_interet;
--ALTER TABLE interet_sous_categorie RENAME TO ref_interet_sous_categorie;
--ALTER TABLE specialite RENAME TO ref_specialite;

--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3 (Debian 16.3-1.pgdg120+1)
-- Dumped by pg_dump version 16.4 (Homebrew)

--
-- Name: ref_baccalaureat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_baccalaureat (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_externe character varying(255) NOT NULL
);


--
-- Name: ref_critere_analyse_candidature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_critere_analyse_candidature (
    id character varying(255) NOT NULL,
    index integer,
    nom character varying(255) NOT NULL
);


--
-- Name: ref_domaine; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_domaine (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_categorie character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL
);

--
-- Name: ref_domaine_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_domaine_categorie (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL
);


--
-- Name: ref_formation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_formation (
    id character varying(255) NOT NULL,
    label character varying(1023),
    descriptif_general text,
    descriptif_attendu text,
    mots_clefs character varying(300)[],
    descriptif_conseils text,
    descriptif_diplome text,
    formations_associees character varying(20)[],
    criteres_analyse integer[],
    liens jsonb,
    apprentissage boolean,
    capacite integer,
    duree integer,
    label_details character varying(1023),
    las character varying(255),
    metiers character varying(255)[],
    stats jsonb,
    voeux character varying(255)[]
);


--
-- Name: ref_interet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_interet (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_sous_categorie character varying(255) NOT NULL
);


--
-- Name: ref_interet_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_interet_categorie (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL,
    id_categorie character varying(255)
);


--
-- Name: ref_interet_sous_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_interet_sous_categorie (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_categorie character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL
);


--
-- Name: ref_join_baccalaureat_specialite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_join_baccalaureat_specialite (
    id_baccalaureat character varying(255) NOT NULL,
    id_specialite character varying(255) NOT NULL
);


--
-- Name: ref_metier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_metier (
    id character varying(255) NOT NULL,
    label character varying(1023) NOT NULL,
    descriptif_general text,
    liens jsonb
);


--
-- Name: ref_moyenne_generale_admis; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_moyenne_generale_admis (
    annee character varying(255) NOT NULL,
    id_formation character varying(255) NOT NULL,
    id_bac character varying(255) NOT NULL,
    frequences_cumulees integer[] NOT NULL
);


--
-- Name: ref_specialite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_specialite (
    id character varying(255) NOT NULL,
    label character varying(255) NOT NULL
);


--
-- Name: ref_triplet_affectation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ref_triplet_affectation (
    id character varying(255) NOT NULL,
    nom character varying(1023) NOT NULL,
    commune character varying(255) NOT NULL,
    code_commune character varying(255) NOT NULL,
    id_formation character varying(255) NOT NULL,
    capacite integer,
    descriptif jsonb,
    latitude double precision,
    longitude double precision,
    coordonnees_geographiques FLOAT[2]
);


--
-- Name: sugg_candidats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sugg_candidats (
    id bigint NOT NULL,
    bac character varying(255),
    voeux character varying(255)[]
);


--
-- Name: sugg_candidats_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE sugg_candidats_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sugg_edges; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sugg_edges (
    id bigint NOT NULL,
    dst character varying(255),
    src character varying(255),
    type integer NOT NULL
);

--
-- Name: sugg_edges_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE sugg_edges_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: sugg_labels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sugg_labels (
    id character varying(255) NOT NULL,
    label character varying(1023),
    label_debug character varying(1023)
);

--
-- Name: sugg_matieres; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sugg_matieres (
    id character varying(255) NOT NULL,
    bacs character varying(255)[],
    est_specialite boolean NOT NULL,
    id_psup integer NOT NULL,
    label character varying(255)
);


--
-- Name: sugg_villes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sugg_villes (
    id character varying(255) NOT NULL,
    coords jsonb
);

--
-- Name: ref_critere_analyse_candidature ref_critere_analyse_candidature_index_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_critere_analyse_candidature
    ADD CONSTRAINT ref_critere_analyse_candidature_index_key UNIQUE (index);


--
-- Name: ref_critere_analyse_candidature ref_critere_analyse_candidature_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_critere_analyse_candidature
    ADD CONSTRAINT ref_critere_analyse_candidature_pkey PRIMARY KEY (id);


--
-- Name: ref_domaine domaine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_domaine
    ADD CONSTRAINT ref_domaine_pkey PRIMARY KEY (id);


--
-- Name: ref_formation formation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_formation
    ADD CONSTRAINT ref_formation_pkey PRIMARY KEY (id);


--
-- Name: ref_domaine_categorie groupement_domaine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_domaine_categorie
    ADD CONSTRAINT ref_groupement_domaine_pkey PRIMARY KEY (id);


--
-- Name: ref_interet_categorie groupement_interet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_interet_categorie
    ADD CONSTRAINT ref_groupement_interet_pkey PRIMARY KEY (id);


--
-- Name: ref_interet_sous_categorie interet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_interet_sous_categorie
    ADD CONSTRAINT ref_interet_pkey PRIMARY KEY (id);


--
-- Name: ref_interet interet_pkey1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_interet
    ADD CONSTRAINT ref_interet_pkey1 PRIMARY KEY (id);


--
-- Name: ref_join_baccalaureat_specialite join_baccalaureat_specialite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_join_baccalaureat_specialite
    ADD CONSTRAINT ref_join_baccalaureat_specialite_pkey PRIMARY KEY (id_baccalaureat, id_specialite);


--
-- Name: ref_metier metier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_metier
    ADD CONSTRAINT ref_metier_pkey PRIMARY KEY (id);


--
-- Name: ref_moyenne_generale_admis moyenne_generale_admis_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_moyenne_generale_admis
    ADD CONSTRAINT ref_moyenne_generale_admis_pkey PRIMARY KEY (annee, id_formation, id_bac);


--
-- Name: ref_specialite specialite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_specialite
    ADD CONSTRAINT ref_specialite_pkey PRIMARY KEY (id);


--
-- Name: sugg_candidats sugg_candidats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sugg_candidats
    ADD CONSTRAINT sugg_candidats_pkey PRIMARY KEY (id);


--
-- Name: sugg_edges sugg_edges_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sugg_edges
    ADD CONSTRAINT sugg_edges_pkey PRIMARY KEY (id);


--
-- Name: sugg_labels sugg_labels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sugg_labels
    ADD CONSTRAINT sugg_labels_pkey PRIMARY KEY (id);


--
-- Name: sugg_matieres sugg_matieres_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sugg_matieres
    ADD CONSTRAINT sugg_matieres_pkey PRIMARY KEY (id);


--
-- Name: sugg_villes sugg_villes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sugg_villes
    ADD CONSTRAINT sugg_villes_pkey PRIMARY KEY (id);


--
-- Name: ref_triplet_affectation triplet_affectation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_triplet_affectation
    ADD CONSTRAINT ref_triplet_affectation_pkey PRIMARY KEY (id);


--
-- Name: ref_baccalaureat type_baccalaureat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_baccalaureat
    ADD CONSTRAINT ref_type_baccalaureat_pkey PRIMARY KEY (id);


--
-- Name: triplet_affectation_id_formation_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ref_triplet_affectation_id_formation_idx ON ref_triplet_affectation USING btree (id_formation);


--
-- Name: ref_domaine domaine_id_groupement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_domaine
    ADD CONSTRAINT ref_domaine_id_groupement_fkey FOREIGN KEY (id_categorie) REFERENCES ref_domaine_categorie(id);


--
-- Name: ref_interet_sous_categorie interet_id_groupement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_interet_sous_categorie
    ADD CONSTRAINT ref_interet_id_groupement_fkey FOREIGN KEY (id_categorie) REFERENCES ref_interet_categorie(id);


--
-- Name: ref_interet interet_id_sous_categorie_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_interet
    ADD CONSTRAINT ref_interet_id_sous_categorie_fkey FOREIGN KEY (id_sous_categorie) REFERENCES ref_interet_sous_categorie(id);


--
-- Name: ref_join_baccalaureat_specialite join_baccalaureat_specialite_id_baccalaureat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_join_baccalaureat_specialite
    ADD CONSTRAINT ref_join_baccalaureat_specialite_id_baccalaureat_fkey FOREIGN KEY (id_baccalaureat) REFERENCES ref_baccalaureat(id);


--
-- Name: ref_join_baccalaureat_specialite join_baccalaureat_specialite_id_specialite_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_join_baccalaureat_specialite
    ADD CONSTRAINT ref_join_baccalaureat_specialite_id_specialite_fkey FOREIGN KEY (id_specialite) REFERENCES ref_specialite(id);


--
-- Name: ref_moyenne_generale_admis moyenne_generale_admis_id_bac_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_moyenne_generale_admis
    ADD CONSTRAINT ref_moyenne_generale_admis_id_bac_fkey FOREIGN KEY (id_bac) REFERENCES ref_baccalaureat(id);


--
-- Name: ref_moyenne_generale_admis moyenne_generale_admis_id_formation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_moyenne_generale_admis
    ADD CONSTRAINT ref_moyenne_generale_admis_id_formation_fkey FOREIGN KEY (id_formation) REFERENCES ref_formation(id);


--
-- Name: ref_triplet_affectation triplet_affectation_id_formation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ref_triplet_affectation
    ADD CONSTRAINT ref_triplet_affectation_id_formation_fkey FOREIGN KEY (id_formation) REFERENCES ref_formation(id);


--
-- PostgreSQL database dump complete
--
