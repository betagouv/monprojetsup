const pages = {
  ACCUEIL: "Accueil",
  PLAN_DU_SITE: "Plan du site",
  ACCESSIBILITÉ: "Accessibilité: non conforme",
  MENTIONS_LÉGALES: "Mentions légales",
  DONNÉES_PERSONNELLES: "Données personnelles",
  GESTION_COOKIES: "Gestion des cookies",
} as const;

const app = {
  NOM: "Mon Projet Sup",
  DESCRIPTION: "Le guide qui facilite l’orientation des lycéens",
} as const;

export const localeFR = {
  APP: {
    NOM: app.NOM,
  },
  ENTÊTE: {
    DESCRIPTION_SERVICE: app.DESCRIPTION,
  },
  PIED_DE_PAGE: {
    DESCRIPTION_SERVICE: app.DESCRIPTION,
    LIENS_INTERNES: {
      PLAN_DU_SITE: pages.PLAN_DU_SITE,
      ACCESSIBILITÉ: pages.ACCESSIBILITÉ,
      MENTIONS_LÉGALES: pages.MENTIONS_LÉGALES,
      DONNÉES_PERSONNELLES: pages.DONNÉES_PERSONNELLES,
      GESTION_COOKIES: pages.GESTION_COOKIES,
    },
  },
  NAVIGATION: {
    ACCUEIL: pages.ACCUEIL,
  },
  PAGE_RECHERCHE: {
    TITRE: "Rechercher une formation ou un métier",
    EXEMPLES_MÉTIERS: "Exemples de métiers accessibles après cette formation",
    FORMATIONS_POUR_APPRENDRE_METIER: "formations pour apprendre le métier",
  },
  PAGE_FORMATION: {
    ONGLET_FORMATION: "La formation",
    ONGLET_DÉTAILS: "Plus de détails",
    ONGLET_CRITÈRES: "Critères d'admission",
    ONGLET_CONSEILS: "Nos conseils",
    ÉLÈVES_ADMIS_ANNÉE_PRÉCÉDENTE: "lycéens ont intégré cette formation l'année dernière",
    VILLES_PROPOSANT_LA_FORMATION: "Villes où trouver cette formation",
    VOIR_SUR_PARCOURSUP: "Voir sur la carte Parcoursup",
    RÉPARTITION_PAR_BAC: "Répartition par série de bacs",
    CRITÈRES_ANALYSE: "Les principaux points examinés dans les candidatures",
    MOYENNE_GÉNÉRALE: "Moyenne générale des lycéens admis à la formation",
    LES_ATTENDUS: "Les attendus de la formation",
    RÉPARTITION_MOYENNE: {
      PREMIER_DÉCILE: "5% des lycéens admis avaient une moyenne générale inférieure à",
      SECOND_DÉCILE: "20% des lycéens admis avaient une moyenne générale comprise entre",
      TROISIÈME_DÉCILE: "50% des lycéens admis avaient une moyenne générale comprise entre",
      QUATRIÈME_DÉCILE: "5% des lycéens admis avaient une moyenne générale supérieure à",
    },
    EXEMPLES_MÉTIERS_ACCESSIBLES: "Exemples de métiers accessibles après cette formation",
  },
  ÉLÈVE: {
    PROJET: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Projet supérieur",
        TITRE: "As-tu déjà un projet d’études supérieures ?",
      },
      SITUATION: {
        LÉGENDE: "Ma situation",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Je n'ai pas encore d’idée",
            DESCRIPTION: "Ça tombe bien, MPS te présente des idées d'études selon ton profil.",
            EMOJI: "🥚",
          },
          QUELQUES_PISTES: {
            LABEL: "J’ai déjà quelques pistes d’orientation",
            DESCRIPTION: "Super, MPS va t’aider à affiner ton projet post-bac.",
            EMOJI: "🐣",
          },
          PROJET_PRÉCIS: {
            LABEL: "J'ai déjà un projet précis",
            DESCRIPTION: "Formidable, explorons les différentes possibilités ensemble.",
            EMOJI: "🐥",
          },
        },
      },
    },
    SCOLARITÉ: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Scolarité",
        TITRE: "Dis-nous en plus sur ta scolarité",
      },
      CLASSE: {
        LABEL: "Classe actuelle",
        OPTIONS: {
          SECONDE: {
            LABEL: "Seconde Générale et Technologique",
          },
          SECONDE_STHR: {
            LABEL: "Seconde STHR",
          },
          SECONDE_TMD: {
            LABEL: "Seconde TMD",
          },
          PREMIÈRE: {
            LABEL: "Première",
          },
          TERMINALE: {
            LABEL: "Terminale",
          },
        },
      },
      BAC: {
        LABEL: "Type de bac choisi ou envisagé",
      },
      SPÉCIALITÉS: {
        LABEL: "Spécialités (EDS) choisies ou envisagées",
        DESCRIPTION: "Commence à taper puis sélectionne des spécialités",
        SÉLECTIONNÉS: "Spécialité(s) sélectionnée(s)",
      },
    },
    DOMAINES: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Domaines professionnels",
        TITRE: "Les domaines professionnels qui t'attirent",
      },
      SÉLECTIONNE_AU_MOINS_UN: "Sélectionne au moins un domaine parmi la liste",
    },
    INTÊRETS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Centres d'intêrets",
        TITRE: "Plus tard, je voudrais ...",
      },
      SÉLECTIONNE_AU_MOINS_UN: "Sélectionne au moins un centre d'intêret parmi la liste",
    },
    MÉTIERS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Les métiers qui m'inspirent",
        TITRE: "As-tu déjà quelques idées de métiers ?",
      },
      SITUATION: {
        LÉGENDE: "Mon avancement",
        DESCRIPTION: "Pas de panique, c’est simplement pour comprendre comment MPS peut t’aider.",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Pas pour l’instant",
            DESCRIPTION: "Ce n'est pas grave, MPS est là pour t'accompagner.",
            EMOJI: "🤔",
          },
          QUELQUES_PISTES: {
            LABEL: "J’ai identifié un ou plusieurs métiers",
            EMOJI: "🙂",
          },
        },
      },
      MÉTIERS_ENVISAGÉS: {
        LABEL: "Métiers envisagés",
        DESCRIPTION: "Commence à taper puis ajoute en favoris les métiers qui pourraient t’intéresser",
        SÉLECTIONNÉS: "Métier(s) sélectionné(s)",
      },
    },
    ÉTUDE: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Études supérieures",
        TITRE: "À propos des études supérieures",
      },
      DURÉE_ÉTUDES: {
        LABEL: "Durée des études",
        DESCRIPTION: "Temps d’études que tu envisages après le bac",
        OPTIONS: {
          OPTIONS_OUVERTES: {
            LABEL: "Je garde mes options ouvertes",
          },
          COURTE: {
            LABEL: "Courte - 3 ans ou moins",
          },
          LONGUE: {
            LABEL: "Longue - 5 ans ou plus",
          },
          AUCUNE_IDÉE: {
            LABEL: "Aucune idée pour le moment",
          },
        },
      },
      ALTERNANCE: {
        LABEL: "Ton intérêt pour un cursus en alternance",
        DESCRIPTION: "Formations alternant scolarité et pratique en entreprise",
        OPTIONS: {
          PAS_INTÉRESSÉ: {
            LABEL: "Pas du tout intéressé(e)",
          },
          INDIFFÉRENT: {
            LABEL: "Indifférent(e)",
          },
          INTÉRESSÉ: {
            LABEL: "Intéressé(e)",
          },
          TRÈS_INTÉRESSÉ: {
            LABEL: "Très intéressé(e)",
          },
        },
      },
      SITUATION_VILLES: {
        LÉGENDE: "Où souhaites-tu étudier ?",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Aucune idée",
          },
          QUELQUES_PISTES: {
            LABEL: "J’ai quelques villes en tête",
          },
        },
      },
      VILLES_ENVISAGÉES: {
        LABEL: "Villes",
        DESCRIPTION: "Commence à taper puis sélectionne des villes",
        SÉLECTIONNÉES: "Ville(s) sélectionnée(s)",
      },
    },
    FORMATIONS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Les études ou formations post-bac",
        TITRE: "Y a-t-il des études qui t'attirent ?",
      },
      SITUATION: {
        LÉGENDE: "Mon avancement",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Pas pour l’instant",
            DESCRIPTION: "Pas de souci, MPS va te proposer différentes possibilités",
            EMOJI: "🤔",
          },
          QUELQUES_PISTES: {
            LABEL: "J’ai déjà identifié une ou plusieurs formations post-bac",
            EMOJI: "🙂",
          },
        },
      },
      FORMATIONS_ENVISAGÉES: {
        LABEL: "Formations envisagées",
        DESCRIPTION: "Commence à taper puis ajoute en favoris les formations qui pourraient t’intéresser",
        SÉLECTIONNÉES: "Formation(s) sélectionnée(s)",
      },
    },
    CONFIRMATION_INSCRIPTION: {
      TITRE_PAGE: "Inscription terminée",
      TITRE: "Félicitations",
      SOUS_TITRE: "Ton inscription est terminée 👌",
      CONTENU: "N’hésite pas à enrichir ton profil pour trouver plus facilement ta voie.",
      BOUTON_ACTION: "Découvre ton espace MPS",
      BANDEAU: {
        TITRE: "Besoin d’aide pour t’orienter ?",
        CONTENU: "Ton professeur principal est à ta disposition pour échanger sur ta future orientation.",
      },
    },
    PROFIL: {
      TITRE: "Mon Profil",
    },
    TABLEAU_DE_BORD: {
      TITRE_PAGE: "Tableau de bord",
      TITRE: "Bienvenue dans ton espace MPS",
      MESSAGE_BIENVENUE: "Ravi de te voir connecté 👋",
      CARTES: {
        SUGGESTIONS: {
          TITRE: "Explore les suggestions de formations post-bac",
          SOUS_TITRE: "d'après tes préférences et découvres-en d'autres",
        },
        FAVORIS: {
          TITRE: "Consulte les formations post-bac que tu as sélectionnées",
          SOUS_TITRE: "et consolide tes futurs voeux pour Parcoursup",
        },
        PROFIL: {
          TITRE: "Enrichis ton profil pour améliorer les suggestions",
          SOUS_TITRE: "Plus tu précises tes préférences, plus les formations proposées seront pertinentes.",
        },
      },
      TÉMOIGNAGE: {
        AUTEUR: "Charlotte",
        RÔLE: "De l'équipe MPS",
        SECONDE:
          "Ton objectif d'ici la fin de l'année : avoir ajouté au moins une formation ou un métier dans ta sélection personnelle. Tu peux évidemment en ajouter plus (et dans différents domaines), si tu le souhaites !",
        PREMIÈRE:
          "Ton orientation se construit dans le temps : il est important que tu sélectionnes plusieurs formations post-bac possibles - et surtout, que tu te renseignes concernant les attendus pour pouvoir te préparer !",
        TERMINALE:
          "Il est temps de réaliser concrètement tes projets ! Pour chaque formation post-bac sur MPS dans laquelle tu te projettes, tu devrais avoir au moins 3 favoris sur Parcoursup.",
      },
    },
  },
  PAGE_ACCUEIL: {
    TITLE: pages.ACCUEIL,
  },
  COMMUN: {
    FERMER: "Fermer",
    CHAMPS_MARQUÉS_DU_SYMBOLE: "Les champs marqués du symbole",
    SONT_OBLIGATOIRES: "sont obligatoires.",
    CONTINUER: "Continuer",
    ENREGISTRER: "Enregistrer",
    RECHERCHER: "Rechercher",
    RETOUR: "Retour",
    FORMATION: "Formation",
    MÉTIER: "Métier",
    TAUX_AFFINITÉ: "Taux d'affinité",
    SÉLECTIONNER_OPTION: "Sélectionner une option",
    PRÉCISER_CATÉGORIES: "N’hésite pas à préciser certaines catégories",
    MODIFICATIONS_ENREGISTRÉES: "Modifications enregistrées avec succès.",
    ERREURS_FORMULAIRES: {
      AUCUN_RÉSULTAT: "Aucun résultat ne correspond à la recherche.",
      LISTE_OBLIGATOIRE: "Sélectionne une option parmi la liste.",
    },
    LIRE_SUITE: "Lire la suite",
    MASQUER_SUITE: "Masquer la suite",
    AJOUTER_À_LA_SÉLECTION: "Ajouter à ma sélection",
    PAS_INTÉRESSÉ: "Pas intéressé",
  },
  ACCESSIBILITÉ: {
    LIEN_EXTERNE: "ouvre un lien externe",
    LIEN_EMAIL: "envoyer un email",
    LIEN_TÉLÉPHONE: "composer le numéro",
    RETIRER: "Retirer",
    VOIR_FIL_ARIANE: "Voir le fil d’ariane",
    VOUS_ÊTES_ICI: "Vous êtes ici :",
    MENU_PRINCIPAL: "Menu principal",
    ONGLETS_FORMATION: "Détails sur la formation",
    VERSION_TEXTE_GRAPHIQUE: "Version texte du graphique",
    FERMER_MODALE: "Fermer la fenêtre modale",
  },
} as const;
