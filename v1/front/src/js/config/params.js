export { params };

export const classes = {
  "": "Choisis une option",
  sec: "Seconde Générale et Technologique",
  secSTHR: "Seconde STHR",
  secTMD: "Seconde TMD",
  prem: "Première",
  term: "Terminale",
};

export const bacs = {
  "": "Je ne sais pas",
  Générale: "Série Générale",
  P: "Bac Pro",
  PA: "Bac Pro Agricole",
  S2TMD:
    "Bac Techno S2TMD - Sciences et Techniques du Théâtre de la Musique et de la Danse",
  ST2S: "Bac Techno ST2S - Sciences et technologies de la santé et du social",
  STAV: "Bac Techno STAV - Sciences et Technologies de l\u0027agronomie et du vivant",
  STD2A:
    "Bac Techno STD2A - Sciences Technologiques du Design et des Arts Appliquées",
  STHR: "Bac Techno STHR - Science et Techniques de l\u0027Hôtellerie et de la Restauration",
  STI2D:
    "Bac Techno STI2D - Sciences et Technologies de l\u0027Industrie et du Développement Durable",
  STL: "Bac Techno STL - Sciences et technologie de laboratoire",
  STMG: "Bac Techno STMG - Sciences et Technologies du Management et de la Gestion",
};

export const apps = {
  A: "Très intéressé(e)",
  B: "Intéressé(e)",
  C: "Indifférent(e)",
  D: "Pas du tout intéressé(e)",
};

export const durees = {
  court:
    "Je m'intéresse en priorité aux formations courtes et rapidement professionnalisantes.",
  long: "Je prévois de faire des études longues.",
  indiff: "Je ne sais pas ou je n'ai pas de préférence",
};

const params = {
  anneeStats: "2023",
  parcoursupCarteHTTPAdress:
    //"http://localhost:8081",
    //"https://preprod.parcoursup.fr/Candidat/carte",
    //"https://monprojetsup.fr/carte/mps.html",
    "https://dossier.parcoursup.fr/Candidat/carte",
  onisepSearchHTTPAdress: "https://www.onisep.fr/recherche?text=",
  internetSearchHTTPAdress: "https://www.qwant.com/?l=fr&q=formation ",

  questionGroups: {
    0: "Mon identité",
    10: "Ma classe",
    15: "Ma scolarité",
    50: "Goûts et compétences",
    51: "Centres d'intérêts",
    60: "Où et combien de temps?",
    70: "Idées métiers",
    80: "Idées formations",
    90: "Apprentissage",
  },
  questionsExplained: {
    0: "Tes nom et prénom sont utiles à ton professeur pour te reconnaître dans la liste des élèves de sa classe.",
  },
  questions: [
    {
      category: "profil",
      txt: "Prénom",
      id: "prenom",
      group: "0",
      type: "textarea",
      placeholder: "Ton prénom",
      hideForAnonymous: true,
      startSameLine: true,
      sameLine: true,
      required: true,
    },
    {
      category: "profil",
      txt: "Nom",
      id: "nom",
      group: "0",
      type: "textarea",
      placeholder: "Ton nom",
      hideForAnonymous: true,
      endSameLine: true,
      sameLine: true,
      required: true,
    },
    {
      txt: "Classe actuelle",
      id: "niveau",
      group: "10",
      type: "radio",
      category: "profil",
      placeholder: "Choisis...",
      options: classes,
    },
    {
      category: "profil",
      txt: "Type de Bac choisi ou envisagé",
      placeholder: "Choisis...",
      filtre: "['prem','term'].includes(niveau)",
      type: "radio",
      id: "bac",
      group: "15",
      explain:
        "<p>Ton type de bac est utilisé pour te recommander en priorité les formations que les candidats de même type de bac ont intégrées l'année précédente, sans pour autant en exclure aucune.</p>",
      options: bacs,
    },
    {
      category: "profil",
      txt: "Enseignements de spécialité de terminale choisis ou envisagés",
      txt2: "Enseignements de spécialité envisagés",
      type: "autocomplete",
      explain:
        "<p>Les recommandations pour des formations ayant recruté des candidats de ces spécialités seront plus fréquentes,\
        sans pour autant n'exclure aucune formation.</p>",
      explain2: `<p>Si tu ne sais pas ou ne veux pas répondre, aucun souci, tu peux juste cliquer sur "suivant".</p>`,
      id: "spe_classes",
      group: "15",
      placeholder: "Recherche une spécialité",
      threshold: "0",
    },
    {
      category: "profil",
      txt: "Moyenne générale scolaire estimée en terminale",
      filtre: "niveau == 'term'",
      explain: `<p>Cette question est facultative. Ton auto-évaluation est utilisée pour te recommander en priorité les formations auxquelles\
         tu as les meilleures chances d'accéder, sans pour autant en exclure aucune.</p>
         <p>Si tu choisis de renseigner ton auto-évaluation,
         tu auras accès à des infos sur les moyennes des admis dans chaque formation.</p>\
         <div class="alert alert-info">Pas évident à déterminer? N'hésite pas à en discuter avec ton professeur principal.</div>
         `,
      type: "range",
      id: "moygen",
      group: "15",
      feedback: "true",
    },
    {
      category: "preferences",

      txt: "Tes goûts et tes compétences",
      explain: `<p>Quelles sont les phrases qui te ressemblent le mieux selon toi?
      Tu peux en choisir autant que tu veux.</p>`,
      type: "tierlist",
      id: "interets",
      group: "50",
    },
    {
      category: "preferences",

      txt: "Tes centres d'intérêts",
      explain: `<p>Quels sont les thèmes qui t'intéressent?
      Tu peux en choisir autant que tu veux.</p>`,
      type: "tierlist",
      id: "thematiques",
      group: "51",
    },
    {
      category: "preferences",

      txt: "Villes préférées",
      explain: `Les suggestions proches de tes villes préférées te seront proposées <b>en priorité</b>,
        mais <b>sans exclure les autres villes</b>. <br/>
        Tu peux laisser cette liste vide si tu n'as pas de préférences géographiques.`,
      type: "autocomplete",
      id: "geo_pref",
      group: "60",
      placeholder: "Renseignez le nom d'une ville",
      threshold: "2",
    },
    {
      category: "preferences",
      txt: "Durée des études envisagée",
      placeholder: "Choisissez...",
      type: "radio",
      id: "duree",
      group: "60",
      options: durees,
    },
    {
      category: "preferences",

      txt: "Métiers qui t'intéressent déjà",
      explain: `<p>Tu as déjà identifié des domaines professionnels (marketing, transports, vente, santé,... )
      ou des métiers (ingénieure du son, directrice artistique,...)
      qui t'intéressent?\
         tu peux les ajouter ici. 
         Tu retrouveras tes choix dans l'onglet \"Mes favoris\".</p>`,
      placeholder: "Choisissez...",
      id: "searchbar_metiers",
      group: "70",
      type: "searchbar_metiers",
      source: "metiers",
    },
    {
      category: "preferences",

      txt: "Formations qui t'intéressent déjà",
      explain:
        "<p>Tu as déjà identifié des formations qui t'intéressent?\
         tu peux les ajouter ici.\
        Tu retrouveras tes choix dans l'onglet \"Mes favoris\".</p>",
      placeholder: "Choisissez...",
      id: "searchbar_formations",
      type: "searchbar_formations",
      source: "formations",
      group: "80",
    },
    {
      category: "preferences",
      txt: "Intérêt pour les formations en apprentissage",
      explain:
        "De nombreuses formations sont accessibles en apprentissage, des BTS aux écoles d'ingénieurs.\
      Tout savoir sur les contrats d'apprentissage \
      <a href=\"https://www.onisep.fr/vers-l-emploi/alternance/le-contrat-d-apprentissage-le-contrat-de-professionnalisation/le-contrat-d-apprentissage\">\
      surle site ONISEP </a>.\
      ",
      placeholder: "Choisissez...",
      type: "radio",
      id: "apprentissage",
      group: "90",
      options: apps,
    },
  ],
  questions_account: [],
};
