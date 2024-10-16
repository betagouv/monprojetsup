export const constantes = {
  LIENS: {
    ALTERNANCE: "https://www.parcoursup.gouv.fr/trouver-une-formation/zoom-sur-les-formations-en-apprentissage-1634",
    DONNER_SON_AVIS: "https://grist.numerique.gouv.fr/o/docs/forms/2Saro42MBAB4ZvJYb9fNzV/4",
    AVENIRS: "https://avenirs.onisep.fr/",
  },
  ÉLÈVE: {
    PATH_PARCOURS_INSCRIPTION: "/eleve/inscription/",
  },
  FICHE_FORMATION: {
    POURCENTAGE_MIN_AFFICHAGE_CRITÈRES: 15,
    RAYONS_RECHERCHE_ÉTABLISSEMENTS: [10, 20, 50],
    TAILLE_BOUTONS_ACTIONS: "grand",
    NB_MÉTIERS_À_AFFICHER: 10,
  },
  ÉTABLISSEMENTS: {
    NB_CARACTÈRES_MIN_RECHERCHE: 3,
    NB_MAX_ÉTABLISSEMENTS: 5,
  },
  COMMUNES: {
    NB_CARACTÈRES_MIN_RECHERCHE: 3,
  },
  FORMATIONS: {
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
  },
  MÉTIERS: {
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
  },
  SPÉCIALITÉS: {
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
  },
} as const;
