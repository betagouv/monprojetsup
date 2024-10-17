const pages = {
  TABLEAU_DE_BORD: "Tableau de bord",
  FAVORIS: "Ma sélection",
  PROFIL: "Mon profil",
  PLAN_DU_SITE: "Plan du site",
  ACCESSIBILITÉ: "Accessibilité : non conforme",
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
    POINTS_AFFINITÉ: "raison(s) de t’intéresser à cette formation",
    VILLES_PROPOSANT_FORMATION: "Formation disponible dans",
    VILLES_PROPOSANT_FORMATION_SUITE: "ville(s)",
    MÉTIERS_ACCESSIBLES: "Parmi les métiers accessibles après cette formation",
  },
  PAGE_FAVORIS: {
    TITRE_PAGE: pages.FAVORIS,
    CATÉGORIE: "Catégorie",
    AUCUN_FAVORI: {
      EMOJI: "😅",
      OUPS: "Oups...",
      TEXTE_FORMATIONS: "Aucune formation dans ta sélection.",
      TEXTE_MÉTIERS: "Aucun métier dans ta sélection.",
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
    ONGLET_CRITÈRES: "Critères d’admission",
    ONGLET_CONSEILS: "Nos conseils",
    ÉLÈVES_ADMIS_ANNÉE_PRÉCÉDENTE: "lycéens ont intégré cette formation l’année dernière",
    VILLES_PROPOSANT_FORMATION: "Formation disponible dans",
    VILLES_PROPOSANT_FORMATION_SUITE: "ville(s)",
    VILLES_PROPOSANT_FORMATION_SUITE_SI_CORRESPONDANCE: "dont",
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
    ALTERNANCE: {
      TITRE: "Cette formation est aussi réalisable en alternance",
      TEXTE: "La formation en alternance te permet d'étudier et de travailler en même temps.",
      LIEN: "En savoir plus",
    },
    EXPLICATIONS_CORRESPONDANCE_PROFIL: {
      TITRE: "Pourquoi cette formation pourrait te plaire",
      COMMUNES: "Plusieurs formations disponibles à proximité de",
      BAC: "Idéal si tu as un",
      SPÉCIALITÉS: "Tes choix d’enseignements de spécialités correspondent",
      DURÉE_FORMATION: "Tu as une préférence pour les études",
      ALTERNANCE: "Formation réalisable",
      ALTERNANCE_SUITE: "en alternance",
      FORMATIONS_SIMILAIRES: "Tu as sélectionné",
      INTÉRÊTS_ET_DOMAINES: "Tu as demandé à voir des formations correspondant à",
      ADMISSION_BAC: "Parmi les lycéennes et lycéens admis dans cette formation l’année dernière,",
      ADMISSION_BAC_SUITE: "étaient des bacheliers de série",
      MOYENNE: "Parmi les lycéennes et lycéens admis dans cette formation l’année dernière, de série",
      MOYENNE_SUITE: ", la moitié avait une moyenne au bac dans l’intervalle",
      EXPLICATION_CALCUL: "Détails du calcul du score (mode expert)",
    },
    VOEUX: {
      TITRE: "Dis-nous en plus sur ce choix",
      AMBITIONS: {
        LÉGENDE: "Je dirais que c’est un choix ...",
        PLAN_B: {
          LABEL: "Plan B",
          EMOJI: "🛟",
        },
        RÉALISTE: {
          LABEL: "Réaliste",
          EMOJI: "🎯",
        },
        AMBITIEUX: {
          LABEL: "Ambitieux",
          EMOJI: "🙏",
        },
      },
      ÉTABLISSEMENTS: {
        LÉGENDE: "Etablissements pour lesquels je souhaite candidater",
        LIENS: {
          PARCOURSUP: "Carte Parcoursup",
          PRÉFÉRENCES: "Préférences de villes",
        },
        PAR_COMMUNE: {
          RAYON: "Dans un rayon de",
          VOIR_PLUS: "établissements dans ce rayon, retrouve toute l’offre de formation sur la",
          AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ: "Il n’existe pas d’offres dans un rayon de",
          AUCUN_ÉTABLISSEMENT_À_PROXIMITÉ_SUITE:
            'km autour de cette ville, consulte la carte des formations Parcoursup ou l’onglet "Toutes les villes"',
        },
        TOUTES_LES_COMMUNES: {
          TITRE_ONGLET: "Toutes les villes",
          RAPPEL: "Tu peux paramétrer les villes dans lesquelles tu souhaiterais étudier.",
          LIEN_PRÉFÉRENCES: "Préférences de villes ›",
          LABEL: "Établissements",
          DESCRIPTION:
            "Commence à taper puis sélectionne des établissements. Les résultats retournés sont limités à 5, retrouve toute l'offre de formation sur la carte Parcoursup.",
          SÉLECTIONNÉS: "Établissement(s) sélectionné(s)",
          VOIR_PLUS: "établissement(s) conforme(s) à ta recherche, retrouve toute l’offre de formation sur la",
        },
      },
      COMMENTAIRE: {
        LABEL: "Note additionnelle",
        BOUTON: "Enregistrer",
      },
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
            LABEL: "Je n’ai pas encore d’idée",
            DESCRIPTION: "Ça tombe bien, MPS te présente des idées d’études selon ton profil.",
            EMOJI: "🥚",
          },
          quelques_pistes: {
            LABEL: "J’ai déjà quelques pistes d’orientation",
            DESCRIPTION: "Super, MPS va t’aider à affiner ton projet post-bac.",
            EMOJI: "🐣",
          },
          projet_precis: {
            LABEL: "J’ai déjà un projet précis",
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
      MOYENNE: {
        LABEL: "Estimation de ta moyenne actuelle",
        DESCRIPTION:
          "Ton auto-évaluation est utilisée pour te recommander en priorité les formations auxquelles tu as les meilleures chances d’accéder, sans pour autant en exclure aucune.",
        AUTO_CENSURE: "L’année dernière,",
        AUTO_CENSURE_SUITE: "% des élèves de terminale de série",
        AUTO_CENSURE_SUITE_2: "admis dans un cursus sur Parcoursup avaient une moyenne de",
        AUTO_CENSURE_FIN: "ou moins.",
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
        TITRE: "Les domaines professionnels qui t’attirent",
      },
      SÉLECTIONNE_AU_MOINS_UN: "Sélectionne au moins un domaine parmi la liste",
    },
    INTÉRÊTS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Centres d’intérêts",
        TITRE: "Plus tard, je voudrais ...",
      },
      SÉLECTIONNE_AU_MOINS_UN: "Sélectionne au moins un centre d’intérêt parmi la liste",
    },
    MÉTIERS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Les métiers qui m’inspirent",
        TITRE: "As-tu déjà quelques idées de métiers ?",
      },
      SITUATION: {
        LÉGENDE: "Mon avancement",
        DESCRIPTION: "Pas de panique, c’est simplement pour comprendre comment MPS peut t’aider.",
        OPTIONS: {
          AUCUNE_IDÉE: {
            LABEL: "Pas pour l’instant",
            DESCRIPTION: "Ce n’est pas grave, MPS est là pour t’accompagner.",
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
        TITRE: "Y a-t-il des études qui t’attirent ?",
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
          SOUS_TITRE: "d’après tes préférences",
        },
        FAVORIS: {
          TITRE: "Consulte les formations post-bac que tu as sélectionnées",
          SOUS_TITRE: "et consolide tes futurs voeux pour Parcoursup",
        },
        PROFIL: {
          TITRE: "Enrichis ton profil pour améliorer les suggestions",
          SOUS_TITRE: "Plus tu précises tes préférences, plus les formations proposées seront pertinentes.",
        },
        AVIS: {
          TITRE: "Que penses-tu de MPS ?",
          SOUS_TITRE: "Donne ton avis sur ton expérience en quelques clics",
          BOUTON: "Je donne mon avis",
        },
      },
      TÉMOIGNAGE: {
        AUTEUR: "Charlotte",
        RÔLE: "De l’équipe MPS",
        PHRASE:
          "Bienvenue sur MPS ! Ici tu peux explorer et sélectionner les formations correspondant à tes préférences, et préparer sereinement tes vœux Parcoursup.",
      },
    },
  },
  PAGE_TABLEAU_DE_BORD: {
    TITRE_PAGE: pages.TABLEAU_DE_BORD,
  },
  FORMATIONS_MASQUÉES: {
    TITRE: "Formations masquées",
    BOUTON_NE_MASQUER: "Ne plus masquer",
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
    PRÉCISER_CATÉGORIES: "Sélectionne la ou les catégories qui t’intéressent",
    MODIFICATIONS_ENREGISTRÉES: "Modifications enregistrées avec succès.",
    ERREURS_FORMULAIRES: {
      AUCUN_RÉSULTAT: "Aucun résultat ne correspond à la recherche.",
      LISTE_OBLIGATOIRE: "Sélectionne une option parmi la liste.",
      AU_MOINS_UNE: "Tu dois indiquer au moins une",
      AU_MOINS_UN: "Tu dois indiquer au moins un",
      VALEUR_INVALIDE: "La valeur saisie est invalide",
      AU_MOINS_X_CARACTÈRES: "Saisie au moins",
      MOINS_DE_X_CARACTÈRES: "Tu ne peux pas saisir plus de",
      CARACTÈRES: "caractères",
    },
    LIRE_SUITE: "Lire la suite",
    MASQUER_SUITE: "Masquer la suite",
    AJOUTER_À_MA_SÉLECTION: "Ajouter à ma sélection",
    AJOUTÉ_À_MA_SÉLECTION: "Ajouté à ma sélection",
    SÉLECTIONNÉ: "Sélectionné",
    SUPPRIMER_DE_MA_SÉLECTION: "Plus intéressé",
    NE_PLUS_VOIR: "Ne plus voir",
    AFFICHER_À_NOUVEAU: "Ne plus masquer cette formation",
    BOUTON_AFFICHER_BARRE_LATÉRALE: "Retour aux résultats",
    BOUTON_AFFICHER_CONTENU_PRINCIPAL: "Retour à la fiche",
    NE_VEUT_PAS_RÉPONDRE: "Je n’en ai aucune idée / Je ne veux pas répondre",
  },
  ACCESSIBILITÉ: {
    CHARGEMENT: "Chargement",
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
    ONGLETS_VOEUX_ÉTABLISSEMENTS: "Établissements par ville",
    VERSION_TEXTE_GRAPHIQUE: "Version texte du graphique",
    FERMER_MODALE: "Fermer la fenêtre modale",
    CONTENU: "Contenu",
    PIED_PAGE: "Pied de page",
  },
} as const;
