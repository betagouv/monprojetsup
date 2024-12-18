INSERT INTO parametre (id, statut)
VALUES ('FORCE_FORMATIONS_UPDATE', true)
ON CONFLICT (id)
DO UPDATE SET statut = true;