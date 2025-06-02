CREATE TABLE profil_reference (
    id character varying(200) NOT NULL,
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
    corbeille_formations character varying[] DEFAULT ARRAY[]::character varying[] NOT NULL,
    formations_favorites jsonb,
    voeux_favoris jsonb
);


ALTER TABLE ONLY public.profil_reference
    ADD CONSTRAINT profil_reference_nouvel_id_key UNIQUE (id);

ALTER TABLE ONLY public.profil_reference
    ADD CONSTRAINT profil_reference_pkey PRIMARY KEY (id);
