CREATE SEQUENCE sugg_paniers_voeux_id_seq OWNED BY sugg_paniers_voeux.id;
ALTER TABLE sugg_paniers_voeux ALTER COLUMN id SET DEFAULT nextval('sugg_paniers_voeux_id_seq');

