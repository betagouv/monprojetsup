CREATE TABLE parametre
(
    id     VARCHAR(200) PRIMARY KEY,
    statut BOOLEAN NOT NULL
);

INSERT INTO parametre
VALUES ('ETL_EN_COURS', false);