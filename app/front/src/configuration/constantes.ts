export const constantes = {
  LIENS: {
    ALTERNANCE: "https://www.parcoursup.gouv.fr/trouver-une-formation/zoom-sur-les-formations-en-apprentissage-1634",
    SIX_NIVEAUX_MPS: "https://monprojetsup.fr/niveaux-de-progression-pedagogique-monprojetsup",
    DONNER_SON_AVIS: "https://grist.numerique.gouv.fr/o/docs/forms/2Saro42MBAB4ZvJYb9fNzV/4",
    AVENIRS: "https://avenirs.onisep.fr/",
  },
  EMAIL_CONTACT: "support@monprojetsup.fr",
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
    CHAMP_FORMATIONS_FAVORITES: "formationsFavorites",
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
  },
  MÉTIERS: {
    CHAMP_MÉTIERS_FAVORIS: "métiersFavoris",
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
  },
  SPÉCIALITÉS: {
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
  },
} as const;
