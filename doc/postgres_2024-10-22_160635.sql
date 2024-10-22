--
-- PostgreSQL database dump
--

-- Dumped from database version 15.8 (Debian 15.8-1.pgdg120+1)
-- Dumped by pg_dump version 15.8 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: eleve_compte_parcoursup; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eleve_compte_parcoursup (
    id_parcoursup integer NOT NULL,
    date_mise_a_jour date NOT NULL,
    id_eleve character varying(200) NOT NULL
);


ALTER TABLE public.eleve_compte_parcoursup OWNER TO postgres;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Name: profil_eleve; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.profil_eleve (
    situation character varying(20),
    classe character varying(20),
    id_baccalaureat character varying(20),
    specialites character varying[],
    domaines character varying[],
    centres_interets character varying[],
    metiers_favoris character varying[],
    duree_etudes_prevue character varying,
    alternance character varying,
    communes_favorites jsonb,
    moyenne_generale double precision,
    corbeille_formations character varying[] DEFAULT ARRAY[]::character varying[] NOT NULL,
    formations_favorites jsonb,
    id character varying(200) NOT NULL,
    CONSTRAINT profil_eleve_alternance_check CHECK (((alternance)::text = ANY ((ARRAY['PAS_INTERESSE'::character varying, 'INDIFFERENT'::character varying, 'INTERESSE'::character varying, 'TRES_INTERESSE'::character varying, 'NON_RENSEIGNE'::character varying])::text[]))),
    CONSTRAINT profil_eleve_classe_check CHECK (((classe)::text = ANY ((ARRAY['SECONDE'::character varying, 'PREMIERE'::character varying, 'TERMINALE'::character varying, 'NON_RENSEIGNE'::character varying])::text[]))),
    CONSTRAINT profil_eleve_duree_etudes_prevue_check CHECK (((duree_etudes_prevue)::text = ANY ((ARRAY['INDIFFERENT'::character varying, 'COURTE'::character varying, 'LONGUE'::character varying, 'AUCUNE_IDEE'::character varying, 'NON_RENSEIGNE'::character varying])::text[]))),
    CONSTRAINT profil_eleve_situation_check CHECK (((situation)::text = ANY ((ARRAY['AUCUNE_IDEE'::character varying, 'QUELQUES_PISTES'::character varying, 'PROJET_PRECIS'::character varying])::text[])))
);


ALTER TABLE public.profil_eleve OWNER TO postgres;

--
-- Name: ref_baccalaureat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_baccalaureat (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_externe character varying(255) NOT NULL
);


ALTER TABLE public.ref_baccalaureat OWNER TO postgres;

--
-- Name: ref_critere_analyse_candidature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_critere_analyse_candidature (
    id character varying(255) NOT NULL,
    index integer,
    nom character varying(255) NOT NULL
);


ALTER TABLE public.ref_critere_analyse_candidature OWNER TO postgres;

--
-- Name: ref_domaine; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_domaine (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_categorie character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL,
    description character varying(255)
);


ALTER TABLE public.ref_domaine OWNER TO postgres;

--
-- Name: ref_domaine_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_domaine_categorie (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL
);


ALTER TABLE public.ref_domaine_categorie OWNER TO postgres;

--
-- Name: ref_domaine_ideo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_domaine_ideo (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_domaine_mps character varying(255) NOT NULL
);


ALTER TABLE public.ref_domaine_ideo OWNER TO postgres;

--
-- Name: ref_formation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_formation (
    id character varying(255) NOT NULL,
    label character varying(1023),
    descriptif_general text,
    descriptif_attendu text,
    mots_clefs character varying(300)[],
    descriptif_conseils text,
    descriptif_diplome text,
    formations_psup character varying(20)[],
    criteres_analyse integer[],
    liens jsonb,
    apprentissage boolean,
    capacite integer,
    duree integer,
    label_details character varying(1023),
    las character varying(255),
    stats jsonb,
    voeux character varying(255)[],
    formations_ideo character varying[],
    type_formation character varying(255)
);


ALTER TABLE public.ref_formation OWNER TO postgres;

--
-- Name: ref_interet; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_interet (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_sous_categorie character varying(255) NOT NULL
);


ALTER TABLE public.ref_interet OWNER TO postgres;

--
-- Name: ref_interet_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_interet_categorie (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL
);


ALTER TABLE public.ref_interet_categorie OWNER TO postgres;

--
-- Name: ref_interet_sous_categorie; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_interet_sous_categorie (
    id character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    id_categorie character varying(255) NOT NULL,
    emoji character varying(255) NOT NULL
);


ALTER TABLE public.ref_interet_sous_categorie OWNER TO postgres;

--
-- Name: ref_join_baccalaureat_specialite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_join_baccalaureat_specialite (
    id_baccalaureat character varying(255) NOT NULL,
    id_specialite character varying(255) NOT NULL
);


ALTER TABLE public.ref_join_baccalaureat_specialite OWNER TO postgres;

--
-- Name: ref_join_formation_metier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_join_formation_metier (
    id_formation character varying(255) NOT NULL,
    id_metier character varying(255) NOT NULL
);


ALTER TABLE public.ref_join_formation_metier OWNER TO postgres;

--
-- Name: ref_join_ville_voeu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_join_ville_voeu (
    id_ville character varying(255) NOT NULL,
    distances_voeux_km jsonb NOT NULL
);


ALTER TABLE public.ref_join_ville_voeu OWNER TO postgres;

--
-- Name: ref_metier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_metier (
    id character varying(255) NOT NULL,
    label character varying(1023) NOT NULL,
    descriptif_general text,
    liens jsonb
);


ALTER TABLE public.ref_metier OWNER TO postgres;

--
-- Name: ref_moyenne_generale_admis; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_moyenne_generale_admis (
    annee character varying(255) NOT NULL,
    id_formation character varying(255) NOT NULL,
    id_bac character varying(255) NOT NULL,
    frequences_cumulees integer[] NOT NULL
);


ALTER TABLE public.ref_moyenne_generale_admis OWNER TO postgres;

--
-- Name: ref_specialite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_specialite (
    id character varying(255) NOT NULL,
    label character varying(255) NOT NULL
);


ALTER TABLE public.ref_specialite OWNER TO postgres;

--
-- Name: ref_voeu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ref_voeu (
    id character varying(255) NOT NULL,
    nom character varying(1023) NOT NULL,
    commune character varying(255) NOT NULL,
    code_commune character varying(255) NOT NULL,
    id_formation character varying(255) NOT NULL,
    capacite integer,
    descriptif jsonb,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL
);


ALTER TABLE public.ref_voeu OWNER TO postgres;

--
-- Name: sugg_candidats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sugg_candidats (
    id bigint NOT NULL,
    bac character varying(255),
    voeux character varying(255)[]
);


ALTER TABLE public.sugg_candidats OWNER TO postgres;

--
-- Name: sugg_candidats_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sugg_candidats_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sugg_candidats_seq OWNER TO postgres;

--
-- Name: sugg_config; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sugg_config (
    id uuid NOT NULL,
    active boolean NOT NULL,
    config jsonb NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.sugg_config OWNER TO postgres;

--
-- Name: sugg_edges; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sugg_edges (
    id bigint NOT NULL,
    dst character varying(255),
    src character varying(255),
    type integer NOT NULL
);


ALTER TABLE public.sugg_edges OWNER TO postgres;

--
-- Name: sugg_edges_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sugg_edges_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sugg_edges_seq OWNER TO postgres;

--
-- Name: sugg_labels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sugg_labels (
    id character varying(255) NOT NULL,
    label character varying(1023),
    label_debug character varying(1023)
);


ALTER TABLE public.sugg_labels OWNER TO postgres;

--
-- Name: sugg_matieres; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sugg_matieres (
    id character varying(255) NOT NULL,
    bacs character varying(255)[],
    est_specialite boolean NOT NULL,
    id_psup integer NOT NULL,
    label character varying(255)
);


ALTER TABLE public.sugg_matieres OWNER TO postgres;

--
-- Name: sugg_villes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sugg_villes (
    id character varying(255) NOT NULL,
    coords jsonb,
    nom character varying(255),
    insee character varying(255)
);


ALTER TABLE public.sugg_villes OWNER TO postgres;

--
-- Name: eleve_compte_parcoursup eleve_compte_parcoursup_nouvel_id_eleve_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eleve_compte_parcoursup
    ADD CONSTRAINT eleve_compte_parcoursup_nouvel_id_eleve_key UNIQUE (id_eleve);


--
-- Name: eleve_compte_parcoursup eleve_compte_parcoursup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eleve_compte_parcoursup
    ADD CONSTRAINT eleve_compte_parcoursup_pkey PRIMARY KEY (id_eleve);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: profil_eleve profil_eleve_nouvel_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profil_eleve
    ADD CONSTRAINT profil_eleve_nouvel_id_key UNIQUE (id);


--
-- Name: profil_eleve profil_eleve_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profil_eleve
    ADD CONSTRAINT profil_eleve_pkey PRIMARY KEY (id);


--
-- Name: ref_critere_analyse_candidature ref_critere_analyse_candidature_index_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_critere_analyse_candidature
    ADD CONSTRAINT ref_critere_analyse_candidature_index_key UNIQUE (index);


--
-- Name: ref_critere_analyse_candidature ref_critere_analyse_candidature_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_critere_analyse_candidature
    ADD CONSTRAINT ref_critere_analyse_candidature_pkey PRIMARY KEY (id);


--
-- Name: ref_domaine ref_domaine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_domaine
    ADD CONSTRAINT ref_domaine_pkey PRIMARY KEY (id);


--
-- Name: ref_formation ref_formation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_formation
    ADD CONSTRAINT ref_formation_pkey PRIMARY KEY (id);


--
-- Name: ref_domaine_categorie ref_groupement_domaine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_domaine_categorie
    ADD CONSTRAINT ref_groupement_domaine_pkey PRIMARY KEY (id);


--
-- Name: ref_interet_categorie ref_groupement_interet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_interet_categorie
    ADD CONSTRAINT ref_groupement_interet_pkey PRIMARY KEY (id);


--
-- Name: ref_interet_sous_categorie ref_interet_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_interet_sous_categorie
    ADD CONSTRAINT ref_interet_pkey PRIMARY KEY (id);


--
-- Name: ref_interet ref_interet_pkey1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_interet
    ADD CONSTRAINT ref_interet_pkey1 PRIMARY KEY (id);


--
-- Name: ref_join_baccalaureat_specialite ref_join_baccalaureat_specialite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_baccalaureat_specialite
    ADD CONSTRAINT ref_join_baccalaureat_specialite_pkey PRIMARY KEY (id_baccalaureat, id_specialite);


--
-- Name: ref_join_formation_metier ref_join_formation_metier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_formation_metier
    ADD CONSTRAINT ref_join_formation_metier_pkey PRIMARY KEY (id_formation, id_metier);


--
-- Name: ref_join_ville_voeu ref_join_ville_voeu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_ville_voeu
    ADD CONSTRAINT ref_join_ville_voeu_pkey PRIMARY KEY (id_ville);


--
-- Name: ref_metier ref_metier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_metier
    ADD CONSTRAINT ref_metier_pkey PRIMARY KEY (id);


--
-- Name: ref_moyenne_generale_admis ref_moyenne_generale_admis_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_moyenne_generale_admis
    ADD CONSTRAINT ref_moyenne_generale_admis_pkey PRIMARY KEY (annee, id_formation, id_bac);


--
-- Name: ref_specialite ref_specialite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_specialite
    ADD CONSTRAINT ref_specialite_pkey PRIMARY KEY (id);


--
-- Name: ref_voeu ref_triplet_affectation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_voeu
    ADD CONSTRAINT ref_triplet_affectation_pkey PRIMARY KEY (id);


--
-- Name: ref_baccalaureat ref_type_baccalaureat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_baccalaureat
    ADD CONSTRAINT ref_type_baccalaureat_pkey PRIMARY KEY (id);


--
-- Name: sugg_candidats sugg_candidats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sugg_candidats
    ADD CONSTRAINT sugg_candidats_pkey PRIMARY KEY (id);


--
-- Name: sugg_config sugg_config_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sugg_config
    ADD CONSTRAINT sugg_config_pkey PRIMARY KEY (id);


--
-- Name: sugg_edges sugg_edges_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sugg_edges
    ADD CONSTRAINT sugg_edges_pkey PRIMARY KEY (id);


--
-- Name: sugg_labels sugg_labels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sugg_labels
    ADD CONSTRAINT sugg_labels_pkey PRIMARY KEY (id);


--
-- Name: sugg_matieres sugg_matieres_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sugg_matieres
    ADD CONSTRAINT sugg_matieres_pkey PRIMARY KEY (id);


--
-- Name: sugg_villes sugg_villes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sugg_villes
    ADD CONSTRAINT sugg_villes_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: ref_join_formation_metier_formation_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ref_join_formation_metier_formation_id ON public.ref_join_formation_metier USING btree (id_formation);


--
-- Name: ref_join_formation_metier_metier_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ref_join_formation_metier_metier_id ON public.ref_join_formation_metier USING btree (id_metier);


--
-- Name: ref_triplet_affectation_id_formation_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ref_triplet_affectation_id_formation_idx ON public.ref_voeu USING btree (id_formation);


--
-- Name: eleve_compte_parcoursup eleve_compte_parcoursup_id_eleve_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eleve_compte_parcoursup
    ADD CONSTRAINT eleve_compte_parcoursup_id_eleve_fkey FOREIGN KEY (id_eleve) REFERENCES public.profil_eleve(id);


--
-- Name: profil_eleve profil_eleve_id_ref_baccalaureat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profil_eleve
    ADD CONSTRAINT profil_eleve_id_ref_baccalaureat_fkey FOREIGN KEY (id_baccalaureat) REFERENCES public.ref_baccalaureat(id);


--
-- Name: ref_domaine ref_domaine_id_groupement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_domaine
    ADD CONSTRAINT ref_domaine_id_groupement_fkey FOREIGN KEY (id_categorie) REFERENCES public.ref_domaine_categorie(id);


--
-- Name: ref_interet_sous_categorie ref_interet_id_groupement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_interet_sous_categorie
    ADD CONSTRAINT ref_interet_id_groupement_fkey FOREIGN KEY (id_categorie) REFERENCES public.ref_interet_categorie(id);


--
-- Name: ref_interet ref_interet_id_sous_categorie_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_interet
    ADD CONSTRAINT ref_interet_id_sous_categorie_fkey FOREIGN KEY (id_sous_categorie) REFERENCES public.ref_interet_sous_categorie(id);


--
-- Name: ref_join_baccalaureat_specialite ref_join_baccalaureat_specialite_id_baccalaureat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_baccalaureat_specialite
    ADD CONSTRAINT ref_join_baccalaureat_specialite_id_baccalaureat_fkey FOREIGN KEY (id_baccalaureat) REFERENCES public.ref_baccalaureat(id);


--
-- Name: ref_join_baccalaureat_specialite ref_join_baccalaureat_specialite_id_specialite_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_baccalaureat_specialite
    ADD CONSTRAINT ref_join_baccalaureat_specialite_id_specialite_fkey FOREIGN KEY (id_specialite) REFERENCES public.ref_specialite(id);


--
-- Name: ref_join_formation_metier ref_join_formation_metier_formation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_formation_metier
    ADD CONSTRAINT ref_join_formation_metier_formation_fkey FOREIGN KEY (id_formation) REFERENCES public.ref_formation(id);


--
-- Name: ref_join_formation_metier ref_join_formation_metier_metier_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_join_formation_metier
    ADD CONSTRAINT ref_join_formation_metier_metier_fkey FOREIGN KEY (id_metier) REFERENCES public.ref_metier(id);


--
-- Name: ref_moyenne_generale_admis ref_moyenne_generale_admis_id_bac_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_moyenne_generale_admis
    ADD CONSTRAINT ref_moyenne_generale_admis_id_bac_fkey FOREIGN KEY (id_bac) REFERENCES public.ref_baccalaureat(id);


--
-- Name: ref_moyenne_generale_admis ref_moyenne_generale_admis_id_formation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_moyenne_generale_admis
    ADD CONSTRAINT ref_moyenne_generale_admis_id_formation_fkey FOREIGN KEY (id_formation) REFERENCES public.ref_formation(id);


--
-- Name: ref_voeu ref_triplet_affectation_id_formation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ref_voeu
    ADD CONSTRAINT ref_triplet_affectation_id_formation_fkey FOREIGN KEY (id_formation) REFERENCES public.ref_formation(id);


--
-- PostgreSQL database dump complete
--

