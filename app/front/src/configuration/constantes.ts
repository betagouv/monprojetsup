export const constantes = {
  LIENS: {
    ALTERNANCE: "https://www.parcoursup.gouv.fr/trouver-une-formation/zoom-sur-les-formations-en-apprentissage-1634",
    SIX_NIVEAUX_MPS: "https://monprojetsup.fr/niveaux-de-progression-pedagogique-monprojetsup",
    AVENIRS: "https://avenirs.onisep.fr/",
    FICHE_VOEU_PARCOURSUP: "https://dossierappel.parcoursup.fr/Candidats/public/fiches/afficherFicheFormation",
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
    NB_CARACTÈRES_MAX_COMMENTAIRE: 4_000,
  },
  FICHE_MÉTIER: {
    NB_FORMATIONS_À_AFFICHER: 5,
  },
  ÉTABLISSEMENTS: {
    NB_CARACTÈRES_MIN_RECHERCHE: 3,
    PAGINATION_ÉTABLISSEMENTS: 5,
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
