/* eslint-disable @typescript-eslint/require-await */
import { type FicheFormation, Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";

export class formationInMemoryRepository implements FormationRepository {
  private _FORMATIONS: FicheFormation[] = [
    {
      id: "fl1002093",
      nom: "L1 - Tourisme - Accès Santé (LAS)",
      descriptifs: {
        formation:
          "La licence tourisme te permet de devenir un ou une experte du tourisme en 3 ans, même s'il est recommandé de poursuivre tes études pour te spécialiser après ce diplôme. Au programme, tu auras donc du marketing et du management, de la vente de productions touristiques, de la gestion de la relation clients, de la stratégie, du tourisme d'affaires, des langues... de quoi connaître tous les aspects du secteur avant de te spécialiser au fur et à mesure !",
        détails:
          "Une licence est un diplôme qui se prépare en 3 ans, divisés en 6 semestre, à l'université. En faisant une licence de sciences humaines et sociales, tu vas t'orienter vers les métiers du social, bien sûr, mais aussi du secteur public, ou encore du paramédical. La particualité de la fac réside dans la méthode d'enseignements : des cours principalement théoriques, avec un mélange de cours dits magistraux, où se retrouvent plusieurs centaines d'étudiants qui suivent l'enseignement d'un professeur souvent très connu dans sa matière, et des cours en plus petits groupes qui permettent d'approfondir le cours magistral ou de suivre des enseignements de spécialisation. A la fin de chaque semestre, tu devras passer des examens. Une fois ton diplôme de licence en poche, tu peux entrer directement sur le marché du travail ou bien poursuivre tes études vers un master ou en école spécialisée (les sélections se font sur dossier).",
        attendus: "",
        conseils: "",
      },
      estEnAlternance: false,
      lienParcoursSup: "https://dossier.parcoursup.fr/Candidat/carte?search=tourisme",
      liens: [
        {
          intitulé: "Fiche détaillée Onisep - Les études de santé",
          url: "https://explorer-avenirs.onisep.fr/formation/les-principaux-domaines-de-formation/les-etudes-de-sante/les-voies-d-acces-aux-etudes-de-maieutique-medecine-odontologie-pharmacie",
        },
        {
          intitulé: "Fiche détaillée Onisep - licence mention tourisme",
          url: "https://explorer-avenirs.onisep.fr/http/redirection/formation/slug/FOR.6963",
        },
        {
          intitulé: "Voir sur la carte Parcoursup - Tourisme",
          url: "https://dossier.parcoursup.fr/Candidat/carte?search=tourisme",
        },
        {
          intitulé: "Voir sur la carte Parcoursup - Tourisme -  Accès Santé (LAS)",
          url: "https://dossier.parcoursup.fr/Candidat/carte?search=tourisme++++acces+sante++las+",
        },
      ],
      admis: {
        moyenneGénérale: {
          idBac: null,
          nomBac: null,
          centiles: [],
        },
        répartition: {
          parBac: [
            {
              idBac: "Générale",
              nomBac: "Bac Général",
              nombre: 10,
              pourcentage: 100,
            },
          ],
        },
        total: 10,
      },
      formationsAssociées: ["fl1002093"],
      critèresAnalyse: [
        {
          nom: "Résultats académiques",
          pourcentage: 25,
        },
        {
          nom: "Compétences académiques",
          pourcentage: 25,
        },
        {
          nom: "Savoir-être",
          pourcentage: 20,
        },
        {
          nom: "Motivation, connaissance",
          pourcentage: 25,
        },
        {
          nom: "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
          pourcentage: 5,
        },
      ],
      voeux: [
        {
          id: "ta35729",
          nom: "Université Angers (Angers) - Tourisme - Hospitalité -  Accès Santé (LAS) - Hospitalité",
          commune: {
            nom: "Angers",
            code: "49007",
          },
        },
        {
          id: "ta35732",
          nom: "Université Angers (Angers) - Tourisme -  Accès Santé (LAS)",
          commune: {
            nom: "Angers",
            code: "49007",
          },
        },
      ],
      voeuxParCommuneFavorites: [
        {
          commune: {
            code: "69123",
            nom: "Lyon",
          },
          voeux: [],
        },
        {
          commune: {
            code: "11225",
            nom: "Mas-Saintes-Puelles",
          },
          voeux: [],
        },
        {
          commune: {
            code: "13055",
            nom: "Marseille",
          },
          voeux: [],
        },
      ],
      communesProposantLaFormation: ["Angers"],
      métiersAccessibles: [
        {
          id: "MET_815",
          nom: "Chef/fe de produit touristique",
          descriptif:
            "Treck au Népal, safari au Kenya, circuit culturel en Égypte... armé de son téléphone et de son ordinateur, le chef de produit touristique concocte derrière son bureau des voyages de rêve, pour le plaisir des autres.",
          liens: [
            {
              intitulé: "Fiche détaillée Onisep - chef/fe de produit touristique",
              url: "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.753",
            },
            {
              intitulé: "France Travail - Conception de produits touristiques",
              url: "https://candidat.francetravail.fr/metierscope/fiche-metier/G1301",
            },
          ],
        },
        {
          id: "MET.434",
          nom: "Directeur/trice d'office de tourisme",
          descriptif:
            "Le directeur d'office de tourisme participe à la promotion d'une ville, d'une région, etc. Il centralise l'offre touristique de son territoire et développe des produits pour les différents publics, en concertation avec les professionnels et les élus.",
          liens: [
            {
              intitulé: "Fiche détaillée Onisep - directeur/trice d'office de tourisme",
              url: "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.434",
            },
            {
              intitulé: "France Travail - Promotion du tourisme local",
              url: "https://candidat.francetravail.fr/metierscope/fiche-metier/G1102",
            },
          ],
        },
        {
          id: "MET.2",
          nom: "Directeur/trice d'hôtel",
          descriptif:
            "Animateur d'équipe, gestionnaire et commercial, le directeur d'hôtel a tout du chef d'entreprise. Ses responsabilités varient fortement selon le type d'établissement qu'il dirige, mais exigent une présence de tous les instants.",
          liens: [
            {
              intitulé: "Fiche détaillée Onisep - directeur/trice d'hôtel",
              url: "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.2",
            },
            {
              intitulé: "France Travail - Management d'hôtel-restaurant",
              url: "https://candidat.francetravail.fr/metierscope/fiche-metier/G1402",
            },
          ],
        },
      ],
      explications: {
        communes: [],
        formationsSimilaires: [],
        duréeÉtudesPrévue: null,
        alternance: null,
        choixÉlève: {
          intérêts: [],
          domaines: [],
          métiers: [],
        },
        spécialitésChoisies: [],
        typeBaccalaureat: {
          id: "Générale",
          nom: "Bac Général",
          pourcentageAdmisAnnéePrécédente: 100,
        },
        autoEvaluationMoyenne: null,
        explicationsCalcul: [],
      },
      affinité: 1,
    },
    {
      id: "fl1464",
      nom: "BTS - Tourisme - en apprentissage",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      lienParcoursSup: null,
      estEnAlternance: false,
      liens: [],
      communesProposantLaFormation: [],
      voeux: [],
      voeuxParCommuneFavorites: [],
      admis: {
        moyenneGénérale: {
          idBac: null,
          nomBac: null,
          centiles: [],
        },
        répartition: { parBac: [] },
        total: null,
      },
      formationsAssociées: [],
      critèresAnalyse: [],
      métiersAccessibles: [],
      affinité: 2,
      explications: null,
    },
    {
      id: "fl2093",
      nom: "L1 - Tourisme",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      lienParcoursSup: null,
      estEnAlternance: true,
      liens: [],
      communesProposantLaFormation: [],
      voeux: [],
      voeuxParCommuneFavorites: [],
      admis: {
        moyenneGénérale: {
          idBac: null,
          nomBac: null,
          centiles: [],
        },
        répartition: { parBac: [] },
        total: null,
      },
      formationsAssociées: [],
      critèresAnalyse: [],
      métiersAccessibles: [],
      affinité: 0,
      explications: null,
    },
    {
      id: "fl464",
      nom: "BTS - Tourisme",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      lienParcoursSup: null,
      estEnAlternance: true,
      liens: [],
      communesProposantLaFormation: [],
      voeux: [],
      voeuxParCommuneFavorites: [],
      admis: {
        moyenneGénérale: {
          idBac: null,
          nomBac: null,
          centiles: [],
        },
        répartition: { parBac: [] },
        total: null,
      },
      formationsAssociées: [],
      critèresAnalyse: [],
      métiersAccessibles: [],
      affinité: 2,
      explications: null,
    },
    {
      id: "fl467",
      nom: "BTS - Métiers de l'eau",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      lienParcoursSup: null,
      estEnAlternance: true,
      liens: [],
      communesProposantLaFormation: [],
      voeux: [],
      voeuxParCommuneFavorites: [],
      admis: {
        moyenneGénérale: {
          idBac: null,
          nomBac: null,
          centiles: [],
        },
        répartition: { parBac: [] },
        total: null,
      },
      formationsAssociées: [],
      critèresAnalyse: [],
      métiersAccessibles: [],
      affinité: 5,
      explications: null,
    },
    {
      id: "fl470",
      nom: "BTS - Biotechnologies",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      lienParcoursSup: null,
      liens: [],
      estEnAlternance: true,
      communesProposantLaFormation: [],
      voeux: [],
      voeuxParCommuneFavorites: [],
      admis: {
        moyenneGénérale: {
          idBac: null,
          nomBac: null,
          centiles: [],
        },
        répartition: { parBac: [] },
        total: null,
      },
      formationsAssociées: [],
      critèresAnalyse: [],
      métiersAccessibles: [],
      affinité: 3,
      explications: null,
    },
  ];

  public async récupérerUneFiche(formationId: string): Promise<FicheFormation | Error> {
    return this._FORMATIONS.find((formation) => formation.id === formationId) ?? new RessourceNonTrouvéeErreur();
  }

  public async récupérerPlusieursFiches(formationIds: string[]): Promise<FicheFormation[] | Error> {
    return this._FORMATIONS.filter((formation) => formationIds.includes(formation.id));
  }

  public async récupérerPlusieurs(formationIds: string[]): Promise<Formation[] | Error> {
    return this._FORMATIONS
      .filter((formation) => formationIds.includes(formation.id))
      .map((formation) => ({
        id: formation.id,
        nom: formation.nom,
      }));
  }

  public async rechercherFichesFormations(recherche: string): Promise<FicheFormation[] | Error> {
    return this._FORMATIONS.filter((formation) => formation.nom.toLowerCase().includes(recherche.toLowerCase()));
  }

  public async rechercherFormations(recherche: string): Promise<Formation[] | Error> {
    return this._FORMATIONS
      .filter((formation) => formation.nom.toLowerCase().includes(recherche.toLowerCase()))
      .map((formation) => ({
        id: formation.id,
        nom: formation.nom,
      }));
  }

  public async suggérer(): Promise<FicheFormation[] | Error> {
    return this._FORMATIONS.slice(0, 5);
  }

  public async récupérerToutes(): Promise<FicheFormation[] | Error> {
    return this._FORMATIONS;
  }
}
