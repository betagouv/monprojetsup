ALTER TABLE sugg_paniers_voeux ALTER COLUMN id SET DEFAULT 0;
DROP SEQUENCE IF EXISTS sugg_paniers_voeux_seq;
CREATE SEQUENCE sugg_paniers_voeux_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE sugg_paniers_voeux ALTER COLUMN id SET DEFAULT nextval('sugg_paniers_voeux_seq');