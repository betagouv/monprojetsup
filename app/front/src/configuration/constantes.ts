export const constantes = {
  LIENS: {
    ALTERNANCE: "https://www.parcoursup.gouv.fr/trouver-une-formation/zoom-sur-les-formations-en-apprentissage-1634",
    SIX_NIVEAUX_MPS: "https://monprojetsup.fr/niveaux-de-progression-pedagogique-monprojetsup",
    FICHE_VOEU_PARCOURSUP: "https://dossierappel.parcoursup.fr/Candidats/public/fiches/afficherFicheFormation",
    PAGE_MENTIONS_LÉGALES: "https://monprojetsup.fr/mentions-legales/",
    PAGE_DONNÉES_PERSONNELLES: "https://monprojetsup.fr/donnees-personnelles/",
  },
  CONTACT: {
    EMAIL: "support@monprojetsup.fr",
    ADRESSE: "12 mail Barthélémy Thimonnier, 77437 Marne la Vallée cedex 2 CS 10450",
  },
  ÉLÈVE: {
    PATH_PARCOURS_INSCRIPTION: "/eleve/inscription/",
  },
  ACCESSIBILITÉ: {
    CONTENU_ID: "contenu",
    PIED_DE_PAGE_ID: "pied-de-page",
    FICHE_ID: "fiche",
    LISTE_CARTES_ID: "liste-cartes",
  },
  VOEUX: {
    NB_PAR_PAGE: 5,
    NB_CARACTÈRES_MIN_RECHERCHE: 3,
    NB_CARACTÈRES_MAX_RECHERCHE: 50,
    RAYONS_RECHERCHE: [10, 20, 50],
  },
  COMMUNES: {
    NB_PAR_PAGE: 5,
    NB_CARACTÈRES_MIN_RECHERCHE: 3,
    NB_CARACTÈRES_MAX_RECHERCHE: 50,
  },
  FORMATIONS: {
    CHAMP_FORMATIONS_FAVORITES: "formations",
    NB_PAR_PAGE: 10,
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
    NB_CARACTÈRES_MAX_RECHERCHE: 150,
    CARTES: {
      NB_MÉTIERS_À_AFFICHER: 3,
    },
    FICHES: {
      POURCENTAGE_MIN_AFFICHAGE_CRITÈRES: 15,
      TAILLE_BOUTONS_ACTIONS: "grand",
      NB_MÉTIERS_À_AFFICHER: 10,
      NB_CARACTÈRES_MAX_NOTE_PERSONNELLE: 4_000,
    },
  },
  MÉTIERS: {
    CHAMP_MÉTIERS_FAVORIS: "métiersFavoris",
    NB_PAR_PAGE: 10,
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
    NB_CARACTÈRES_MAX_RECHERCHE: 100,
    FICHES: {
      NB_FORMATIONS_À_AFFICHER: 5,
    },
  },
  SPÉCIALITÉS: {
    NB_PAR_PAGE: 10,
    NB_CARACTÈRES_MIN_RECHERCHE: 2,
    NB_CARACTÈRES_MAX_RECHERCHE: 50,
  },
} as const;
