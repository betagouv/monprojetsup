const pages = {
  TABLEAU_DE_BORD: "Tableau de bord",
  FAVORIS: "Ma sélection",
  PROFIL: "Mon profil",
  PLAN_DU_SITE: "Plan du site",
  ACCESSIBILITÉ: "Accessibilité: non conforme",
  MENTIONS_LÉGALES: "Mentions légales",
  DONNÉES_PERSONNELLES: "Données personnelles",
  GESTION_COOKIES: "Gestion des cookies",
} as const;

const app = {
  NOM: "MonProjetSup",
  DESCRIPTION: "Le guide qui facilite l’orientation des lycéens",
} as const;

export const localeFR = {
  APP: {
    NOM: app.NOM,
  },
  ENTÊTE: {
    DESCRIPTION_SERVICE: app.DESCRIPTION,
    SE_CONNECTER: "Se connecter",
    PLATEFORME_AVENIRS: "Plateforme Avenir(s)",
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
    TABLEAU_DE_BORD: pages.TABLEAU_DE_BORD,
    FORMATIONS: "Explorer les formations",
    FAVORIS: "Consulter ma sélection",
  },
  CARTE_MÉTIER: {
    FORMATIONS: "formation(s) pour apprendre le métier",
  },
  CARTE_FORMATION: {
    POINTS_AFFINITÉ: "point(s) d'affinité avec ton profil",
    FORMATION_DISPONIBLES: "Formation disponible dans",
    FORMATION_DISPONIBLES_SUITE: "ville(s)",
    MÉTIERS_ACCESSIBLES: "Parmi les métiers accessibles après cette formation",
  },
  PAGE_FAVORIS: {
    TITRE_PAGE: pages.FAVORIS,
    CATÉGORIE: "Catégorie",
    AUCUN_FAVORI: {
      EMOJI: "😅",
      OUPS: "Oups...",
      TEXTE: "Aucune sélection ne figure dans ton projet.",
      BOUTON: "Explorer les formations",
    },
    FORMATIONS_POUR_UN_MÉTIER: "Exemples de formations post-bac pour faire ce métier",
    AFFICHER_FORMATIONS_SUPPLÉMENTAIRES: "Afficher les autres formations",
  },
  PAGE_FORMATION: {
    AUCUN_RÉSULTAT: "Aucun résultat trouvé pour cette recherche",
    RETOUR_AUX_SUGGESTIONS: "Retour aux suggestions",
    CHAMP_RECHERCHE_LABEL: "Recherche une formation",
    CHAMP_RECHERCHE_PLACEHOLDER: "Formation, métier, mots clés ...",
    SUGGESTIONS_TRIÉES_AFFINITÉ: "Résultats triés par affinité d’après",
    SUGGESTIONS_TRIÉES_AFFINITÉ_SUITE: "tes préférences ›",
    ONGLET_FORMATION: "La formation",
    ONGLET_DÉTAILS: "Plus de détails",
    ONGLET_CRITÈRES: "Critères d'admission",
    ONGLET_CONSEILS: "Nos conseils",
    ÉLÈVES_ADMIS_ANNÉE_PRÉCÉDENTE: "lycéens ont intégré cette formation l'année dernière",
    FORMATION_DISPONIBLES: "Formation disponible dans",
    FORMATION_DISPONIBLES_SUITE: "ville(s)",
    FORMATION_DISPONIBLES_SUITE_SI_CORRESPONDANCE: "dont",
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
    EXPLICATIONS_CORRESPONDANCE_PROFIL: {
      TITRE: "Pourquoi cette formation pourrait te plaire",
      COMMUNES: "Plusieurs formations disponibles à proximité de",
      BAC: "Idéal si tu as un",
      SPÉCIALITÉS: "Tes choix d’enseignements de spécialités correspondent",
      DURÉE_FORMATION: "Tu as une préférence pour les études",
      ALTERNANCE: "Formation réalisable",
      ALTERNANCE_SUITE: "en alternance",
      FORMATIONS_SIMILAIRES: "Cette formation est similaire à",
      INTÊRETS_ET_DOMAINES: "Tu as demandé à voir des formations correspondants à",
      ADMISSION_BAC: "Parmi les lycéennes et lycéens admis dans cette formation l'année dernière,",
      ADMISSION_BAC_SUITE: "étaient des bacheliers de série",
      MOYENNE: "Parmi les lycéennes et lycéens admis dans cette formation l'année dernière, de série",
      MOYENNE_SUITE: ", la moitié avait une moyenne au bac dans l'intervalle",
    },
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
          aucune_idee: {
            LABEL: "Je n'ai pas encore d’idée",
            DESCRIPTION: "Ça tombe bien, MPS te présente des idées d'études selon ton profil.",
            EMOJI: "🥚",
          },
          quelques_pistes: {
            LABEL: "J’ai déjà quelques pistes d’orientation",
            DESCRIPTION: "Super, MPS va t’aider à affiner ton projet post-bac.",
            EMOJI: "🐣",
          },
          projet_precis: {
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
          seconde: {
            LABEL: "Seconde",
          },
          premiere: {
            LABEL: "Première",
          },
          terminale: {
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
          indifferent: {
            LABEL: "Je garde mes options ouvertes",
          },
          courte: {
            LABEL: "Courte - 3 ans ou moins",
          },
          longue: {
            LABEL: "Longue - 5 ans ou plus",
          },
          aucune_idee: {
            LABEL: "Aucune idée pour le moment",
          },
        },
      },
      ALTERNANCE: {
        LABEL: "Ton intérêt pour un cursus en alternance",
        DESCRIPTION: "Formations alternant scolarité et pratique en entreprise",
        OPTIONS: {
          pas_interesse: {
            LABEL: "Pas du tout intéressé(e)",
          },
          indifferent: {
            LABEL: "Indifférent(e)",
          },
          interesse: {
            LABEL: "Intéressé(e)",
          },
          tres_interesse: {
            LABEL: "Très intéressé(e)",
          },
        },
      },
      COMMUNES_ENVISAGÉES: {
        LABEL: "As-tu des villes particulières où tu souhaites étudier ?",
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
    TABLEAU_DE_BORD: {
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
  PAGE_TABLEAU_DE_BORD: {
    TITRE_PAGE: pages.TABLEAU_DE_BORD,
  },
  PAGE_PROFIL: {
    TITRE_PAGE: pages.PROFIL,
    TITRE: pages.PROFIL,
    SE_DÉCONNECTER: "Se déconnecter",
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
    SÉLECTIONNER_OPTION: "Sélectionner une option",
    PRÉCISER_CATÉGORIES: "Sélectionne la ou les catégories qui t'intéressent",
    MODIFICATIONS_ENREGISTRÉES: "Modifications enregistrées avec succès.",
    ERREURS_FORMULAIRES: {
      AUCUN_RÉSULTAT: "Aucun résultat ne correspond à la recherche.",
      LISTE_OBLIGATOIRE: "Sélectionne une option parmi la liste.",
      AU_MOINS_X_CARACTÈRES: "Saisissez au moins",
      AU_MOINS_X_CARACTÈRES_SUITE: "caractères",
    },
    LIRE_SUITE: "Lire la suite",
    MASQUER_SUITE: "Masquer la suite",
    AJOUTER_À_MA_SÉLECTION: "Ajouter à ma sélection",
    AJOUTÉ_À_MA_SÉLECTION: "Ajouté à ma sélection",
    SUPPRIMER_DE_MA_SÉLECTION: "Plus intéressé",
    NE_PLUS_VOIR: "Ne plus voir",
    AFFICHER_À_NOUVEAU: "Ne plus masquer cette formation",
    BOUTON_AFFICHER_BARRE_LATÉRALE: "Retour aux résultats",
    BOUTON_AFFICHER_CONTENU_PRINCIPAL: "Retour à la fiche",
  },
  ACCESSIBILITÉ: {
    FAVORIS: "Favoris",
    MASQUÉ: "Masqué",
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
    CONTENU: "Contenu",
    PIED_PAGE: "Pied de page",
  },
} as const;
