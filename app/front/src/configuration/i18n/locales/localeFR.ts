const pages = {
  TABLEAU_DE_BORD: "Tableau de bord",
  FAVORIS: "Ma sélection",
  PROFIL: "Mon profil",
  GESTION_COOKIES: "Gestion des cookies",
  EXPLORATION: "Les formations",
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
      DONNÉES_PERSONNELLES: "Données personnelles",
      GESTION_COOKIES: pages.GESTION_COOKIES,
    },
  },
  NAVIGATION: {
    TABLEAU_DE_BORD: pages.TABLEAU_DE_BORD,
    FORMATIONS: "Explorer les formations",
    FAVORIS: "Consulter ma sélection",
    PROFIL: "Enrichir mon profil",
  },
  CARTE_MÉTIER: {
    FORMATION: "formation pour apprendre le métier",
    FORMATIONS: "formations pour apprendre le métier",
  },
  CARTE_FORMATION: {
    POINTS_AFFINITÉ_SINGULIER: "raison de t’intéresser à cette formation",
    POINTS_AFFINITÉ_PLURIEL: "raisons de t’intéresser à cette formation",
    COMMUNES_PROPOSANT_FORMATION: "Formation disponible dans",
    COMMUNES_PROPOSANT_FORMATION_SUITE_SINGULIER: "ville",
    COMMUNES_PROPOSANT_FORMATION_SUITE_PLURIEL: "villes",
    MÉTIERS_ACCESSIBLES: "Parmi les métiers accessibles via cette formation",
  },
  PAGE_COOKIES: {
    TITRE_PAGE: "Cookies",
  },
  PAGE_DÉCLARATION_ACCESSIBILITÉ: {
    TITRE_PAGE: "Déclaration accessibilité",
  },
  PAGE_PLAN_DU_SITE: {
    TITRE_PAGE: "Plan du site",
  },
  PAGE_FAVORIS: {
    TITRE_PAGE: pages.FAVORIS,
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
    TITRE_PAGE: pages.EXPLORATION,
    AUCUN_RÉSULTAT: "Aucun résultat trouvé pour cette recherche",
    RETOUR_AUX_SUGGESTIONS: "Retour aux suggestions",
    CHAMP_RECHERCHE_LABEL: "Recherche une formation",
    CHAMP_RECHERCHE_PLACEHOLDER: "Formation, métier, mots clés ...",
    SUGGESTIONS_TRIÉES_AFFINITÉ: "Résultats triés par affinité d’après",
    SUGGESTIONS_TRIÉES_AFFINITÉ_SUITE: "tes préférences ›",
    SUGGESTIONS_TRIÉES_ALEATOIRE: "Résultats triés aléatoirement... pour les personnaliser complète",
    SUGGESTIONS_TRIÉES_ALEATOIRE_SUITE: "ton profil ›",
    ONGLET_FORMATION: "La formation",
    ONGLET_DÉTAILS: "En général",
    ONGLET_CRITÈRES: "Critères d’admission",
    ONGLET_CONSEILS: "Nos conseils",
    ÉLÈVES_ADMIS_ANNÉE_PRÉCÉDENTE: "lycéens ont intégré cette formation l’année dernière",
    COMMUNES_PROPOSANT_FORMATION: "Formation disponible dans",
    COMMUNES_PROPOSANT_FORMATION_SUITE_SINGULIER: "ville",
    COMMUNES_PROPOSANT_FORMATION_SUITE_PLURIEL: "villes",
    COMMUNES_PROPOSANT_FORMATION_SUITE_SI_CORRESPONDANCE: "dont",
    VOIR_SUR_PARCOURSUP: "Voir sur la carte Parcoursup",
    RÉPARTITION_PAR_BAC: "Profil des lycéens et lycéennes ayant intégré la formation",
    CRITÈRES_ANALYSE: "Les principaux points examinés dans les candidatures",
    MOYENNE_GÉNÉRALE: "Moyenne générale des lycéens admis à la formation",
    LES_ATTENDUS: "Les attendus de la formation",
    RÉPARTITION_MOYENNE: {
      PREMIER_DÉCILE: "5% des lycéens admis avaient une moyenne générale inférieure à",
      SECOND_DÉCILE: "20% des lycéens admis avaient une moyenne générale comprise entre",
      TROISIÈME_DÉCILE: "50% des lycéens admis avaient une moyenne générale comprise entre",
      QUATRIÈME_DÉCILE: "20% des lycéens admis avaient une moyenne générale comprise entre",
      CINQUIÈME_DÉCILE: "5% des lycéens admis avaient une moyenne générale supérieure à",
    },
    EXEMPLES_MÉTIERS_ACCESSIBLES: "Exemples de métiers accessibles via cette formation",
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
      INTÉRÊTS_ET_DOMAINES: "Tu as sélectionné",
      ADMISSION_BAC: "Parmi les lycéennes et lycéens admis dans cette formation l’année dernière,",
      ADMISSION_BAC_SUITE: "étaient des bacheliers de filière",
      MOYENNE: "Parmi les lycéennes et lycéens admis dans cette formation l’année dernière, de filière",
      MOYENNE_SUITE: ", la moitié avait une moyenne au bac dans l’intervalle",
      EXPLICATION_CALCUL: "Détails du calcul du score (mode expert)",
    },
    CHOIX: {
      TITRE: "Dis-nous en plus sur ce choix",
      AMBITIONS: {
        TITRE: "Je dirais que c’est un choix ...",
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
      VOEUX: {
        TITRE: "Établissements pour lesquels je souhaite candidater",
        LIENS: {
          PARCOURSUP: "Carte Parcoursup",
          PRÉFÉRENCES: "Préférences de villes",
        },
        PAR_COMMUNE: {
          TITRE: "Établissements",
          RAYON: "Dans un rayon de",
          VOIR_PLUS: "établissements dans ce rayon, retrouve toute l’offre de formation sur la",
          AUCUN_VOEU_À_PROXIMITÉ: "Il n’existe pas d’offres dans un rayon de",
          AUCUN_VOEU_À_PROXIMITÉ_SUITE:
            'km autour de cette ville, consulte la carte des formations Parcoursup ou l’onglet "Toutes les villes"',
        },
        TOUTES_LES_COMMUNES: {
          TITRE_ONGLET: "Toutes les villes",
          RAPPEL: "Tu peux paramétrer les villes dans lesquelles tu souhaiterais étudier.",
          LIEN_PRÉFÉRENCES: "Préférences de villes ›",
          LABEL: "Établissements",
          DESCRIPTION: "Commence à taper puis sélectionne des établissements",
        },
        MA_SÉLECTION: {
          AUCUN: "Aucun établissement sélectionné en favoris",
        },
      },
      NOTE_PERSONNELLE: {
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
        AUTO_CENSURE_SUITE: "% des élèves de terminale de filière",
        AUTO_CENSURE_SUITE_2: "admis dans un cursus sur Parcoursup avaient une moyenne de",
        AUTO_CENSURE_FIN: "ou moins.",
      },
      SPÉCIALITÉS: {
        LABEL: "Spécialité de bac ou enseignement(s) de spécialité choisi(s) ou envisagé(s)",
        DESCRIPTION: "Commence à taper puis sélectionne des spécialités",
        SÉLECTIONNÉS: "Spécialité(s) sélectionnée(s)",
        MA_SÉLECTION: {
          AUCUNE: "Aucune spécialité sélectionnée",
        },
      },
    },
    DOMAINES: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Domaines professionnels",
        TITRE: "Les domaines professionnels qui t’attirent",
      },
      SÉLECTIONNE_AU_MOINS_UN: "Sélectionne au moins un domaine parmi la liste.",
    },
    INTÉRÊTS: {
      PARCOURS_INSCRIPTION: {
        TITRE_ÉTAPE: "Centres d’intérêt",
        TITRE: "Plus tard, je voudrais ...",
      },
      SÉLECTIONNE_AU_MOINS_UN: "Sélectionne au moins un centre d’intérêt parmi la liste.",
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
        MA_SÉLECTION: {
          AUCUN: "Aucun métier sélectionné",
        },
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
            LABEL: "Indifférent(e) / je ne sais pas",
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
        LABEL: "Y a-t-il des villes près desquelles tu souhaites étudier ?",
        DESCRIPTION: "Commence à taper puis sélectionne des villes",
        SÉLECTIONNÉES: "Ville(s) sélectionnée(s)",
        MA_SÉLECTION: {
          AUCUNE: "Aucune ville sélectionnée en favoris",
        },
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
        MA_SÉLECTION: {
          AUCUNE: "Aucune formation sélectionnée",
        },
      },
    },
    CONFIRMATION_INSCRIPTION: {
      TITRE_PAGE: "Inscription terminée",
      TITRE: "Félicitations",
      SOUS_TITRE: "Ton profil est complété 👌",
      CONTENU: "N’hésite pas à enrichir ton profil pour trouver plus facilement ta voie.",
      BOUTON_ACTION: "Découvre ton espace MPS",
      BANDEAU: {
        TITRE: "Besoin d’aide pour t’orienter ?",
        CONTENU: "Ton professeur principal est à ta disposition pour échanger sur ta future orientation.",
      },
    },
    TABLEAU_DE_BORD: {
      TITRE_CONNECTE: "Bienvenue dans ton espace MPS",
      TITRE_DECONNECTE: "C'est parti!",
      MESSAGE_BIENVENUE_CONNECTE: "Ravi de te voir connecté 👋",
      MESSAGE_BIENVENUE_DECONNECTE: "Bienvenue sur MPS 👋",
      MODALE_PARCOURSUP: {
        TITRE: "Synchronisation avec Parcoursup",
        CONTENU:
          "J’autorise MonProjetSup à accéder à mes favoris enregistrés depuis mon compte Parcoursup. À noter que MonProjetSup n’accède à aucun autre élément de ton dossier Parcoursup. Cette autorisation est valable toute la durée de l'année scolaire en cours, et révocable en écrivant à",
        BOUTON_ACTION: "C'est parti",
      },
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
        PROFIL_VIDE: {
          TITRE: "Renseigne ton profil pour obtenir des suggestions personnalisées",
          SOUS_TITRE:
            "Une fois précisés ton profil et tes préférences, des suggestions personnalisées te seront proposées.",
        },
        CONNECTE_TOI: {
          TITRE: "Connecte-toi à ton compte MPS-Avenir(s)",
          SOUS_TITRE: "En te connectant, tu conserveras tes préférences et tes favoris.",
        },
        AVIS: {
          TITRE: "Que penses-tu de MPS ?",
          SOUS_TITRE: "Donne ton avis sur ton expérience en quelques clics",
          BOUTON: "Je donne mon avis",
        },
        PARCOURSUP: {
          TITRE: "Tu as déjà un compte Parcoursup ?",
          SOUS_TITRE:
            "En synchronisant MPS avec Parcoursup, tu retrouveras dans MPS les formations que tu as déjà mises en favori dans Parcoursup.",
          BOUTON: "Synchroniser",
          ALT_ILLUSTRATION: "MPS & ParcourSup (entrez dans l'enseignement supérieur)",
          SI_SYNCHRO: {
            TITRE: "Tes comptes MPS et Parcoursup sont synchronisés",
            SOUS_TITRE: "En désynchronisant tes comptes, tes favoris Parcoursup ne seront plus importés.",
            BOUTON: "Désynchroniser",
          },
        },
      },
      TOAST_PARCOURSUP: {
        SUCCÈS: {
          TITRE: "Synchronisation réussie",
          DESCRIPTION: "Tes comptes MPS & ParcourSup sont désormais associés.",
        },
        ERREUR: {
          TITRE: "Échec de la synchronisation",
          DESCRIPTION: "Une erreur est survenue, réessaye dans quelques instants.",
        },
      },
      TÉMOIGNAGE: {
        AUTEUR: "Mathilde",
        RÔLE: "De l’équipe MPS",
        PHRASE_NIVEAU: "Tu as atteint le niveau ",
        PHRASE_FELICITATIONS: ", félicitations!",
        PHRASE:
          "Bienvenue sur MPS ! Ici tu peux explorer et sélectionner les formations correspondant à tes préférences, et préparer sereinement tes vœux Parcoursup, en atteignant progressivement les",
        PHRASE_SUITE: "6 niveaux MPS.",
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
    FORMATIONS_MASQUÉES: {
      TITRE: "Formations masquées",
      BOUTON_NE_PLUS_MASQUER: "Ne plus masquer",
      MESSAGE_AUCUNE: "Aucune formation masquée pour le moment !",
    },
    MÉTIERS: {
      TITRE_ONGLET: "Métiers",
    },
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
    DÉTAILS_CATÉGORIES: "Détails des catégories",
    DÉTAILS: "Détails",
    MODIFICATIONS_ENREGISTRÉES: "Modifications enregistrées avec succès.",
    ERREURS_FORMULAIRES: {
      TITRE_GÉNÉRIQUE: "Oups...",
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
    SUPPRIMER_DE_MA_SÉLECTION: "Supprimer de ma sélection",
    NE_PLUS_VOIR: "Ne plus voir",
    AFFICHER_À_NOUVEAU: "Ne plus masquer cette formation",
    BOUTON_AFFICHER_BARRE_LATÉRALE: "Retour aux résultats",
    BOUTON_AFFICHER_CONTENU_PRINCIPAL: "Retour à la fiche",
    NE_VEUT_PAS_RÉPONDRE: "Je n’en ai aucune idée / Je ne veux pas répondre",
    FAVORIS: {
      VOIR_PLUS: "Plus de résultats",
      MA_SÉLÉCTION: "Ma sélection",
    },
  },
  ERREURS: {
    SERVEUR_INDISPONIBLE: {
      EMOJI: "😅",
      TITRE: "Oups, le service est temporairement indisponible",
      SOUS_TITRE: "Essaye de nouveau dans quelques minutes.",
    },
    GÉNÉRIQUE: {
      EMOJI: "🤓",
      TITRE: "Oups, on dirait bien un bug",
      SOUS_TITRE: "On a pris note de l'erreur et on corrige ça au plus vite.",
    },
  },
  ACCESSIBILITÉ: {
    CHARGEMENT: "Chargement",
    FAVORI: "Favori",
    LISTE_SUGGESTIONS_FAVORIS: "Éléments correspondants",
    LISTE_FAVORIS_SÉLECTIONNÉS: "Éléments sélectionnés",
    MASQUÉ: "Masqué",
    METTRE_EN_FAVORI: "Mettre en favori",
    FAVORI_PARCOURSUP: "Favori provenant de Parcoursup",
    LIEN_EXTERNE: "ouvre un lien externe",
    LIEN_EMAIL: "envoyer un email",
    LIEN_TÉLÉPHONE: "composer le numéro",
    RETIRER: "Retirer",
    VOIR_FIL_ARIANE: "Voir le fil d’Ariane",
    VOUS_ÊTES_ICI: "Vous êtes ici :",
    MENU_PRINCIPAL: "Menu principal",
    ONGLETS_FORMATION: "Détails sur la formation",
    ONGLETS_VOEUX: "Établissements par ville",
    VERSION_TEXTE_GRAPHIQUE: "Version texte du graphique",
    FERMER_MODALE: "Fermer la fenêtre modale",
    ACCÈS_RAPIDE: "Accès rapide",
    CONTENU: "Contenu",
    FOCUS_FICHE: "Déplacer focus sur la fiche",
    FOCUS_RÉSULTATS: "Déplacer focus sur les résultats",
    PIED_PAGE: "Pied de page",
    NOUVEAUX_RÉSULATS: "nouveaux résultats",
    LISTE_FORMATIONS: "Liste formations",
    LISTE_MÉTIERS: "Liste métiers",
    CATÉGORIE: "Catégorie",
  },
} as const;
