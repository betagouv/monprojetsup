/* eslint-disable @typescript-eslint/require-await */
import { type FicheFormation, Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";
import { RessourceNonTrouvéeErreur } from "@/services/erreurs/erreurs";

export class formationInMemoryRepository implements FormationRepository {
  private FORMATIONS: FicheFormation[] = [
    {
      id: "fl1002093",
      nom: "L1 - Tourisme -  Accès Santé (LAS)",
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
      affinité: 3,
      explications: null,
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
    return this.FORMATIONS.find((formation) => formation.id === formationId) ?? new RessourceNonTrouvéeErreur();
  }

  public async récupérerPlusieursFiches(formationIds: string[]): Promise<FicheFormation[] | Error> {
    return this.FORMATIONS.filter((formation) => formationIds.includes(formation.id));
  }

  public async récupérerPlusieurs(formationIds: string[]): Promise<Formation[] | Error> {
    return this.FORMATIONS.filter((formation) => formationIds.includes(formation.id)).map((formation) => ({
      id: formation.id,
      nom: formation.nom,
    }));
  }

  public async rechercherFichesFormations(recherche: string): Promise<FicheFormation[] | Error> {
    return this.FORMATIONS.filter((formation) => formation.nom.toLowerCase().includes(recherche.toLowerCase()));
  }

  public async rechercherFormations(recherche: string): Promise<Formation[] | Error> {
    return this.FORMATIONS.filter((formation) => formation.nom.toLowerCase().includes(recherche.toLowerCase())).map(
      (formation) => ({
        id: formation.id,
        nom: formation.nom,
      }),
    );
  }

  public async suggérer(): Promise<FicheFormation[] | Error> {
    return this.FORMATIONS.slice(0, 5);
  }
}
