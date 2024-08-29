import { type Formation } from "@/features/formation/domain/formation.interface";
import { type FormationRepository } from "@/features/formation/infrastructure/formationRepository.interface";

export class formationInMemoryRepository implements FormationRepository {
  private FORMATIONS: Formation[] = [
    {
      id: "fl1002093",
      nom: "L1 - Tourisme -  Accès Santé (LAS)",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      liens: [],
      communes: [],
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
      affinité: null,
      explications: null,
    },
    {
      id: "fl1464",
      nom: "BTS - Tourisme - en apprentissage",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      liens: [],
      communes: [],
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
      affinité: null,
      explications: null,
    },
    {
      id: "fl2093",
      nom: "L1 - Tourisme",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      liens: [],
      communes: [],
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
      affinité: null,
      explications: null,
    },
    {
      id: "fl464",
      nom: "BTS - Tourisme",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      liens: [],
      communes: [],
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
      affinité: null,
      explications: null,
    },
    {
      id: "fl467",
      nom: "BTS - Métiers de l'eau",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      liens: [],
      communes: [],
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
      affinité: null,
      explications: null,
    },
    {
      id: "fl470",
      nom: "BTS - Biotechnologies",
      descriptifs: { formation: null, détails: null, attendus: null, conseils: null },
      liens: [],
      communes: [],
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
      affinité: null,
      explications: null,
    },
  ];

  public async récupérer(formationId: string): Promise<Formation | undefined> {
    return this.FORMATIONS.find((formation) => formation.id === formationId);
  }

  public async récupérerPlusieurs(formationIds: string[]): Promise<Formation[] | undefined> {
    return this.FORMATIONS.filter((formation) => formationIds.includes(formation.id));
  }

  public async rechercher(recherche: string): Promise<Formation[] | undefined> {
    return this.FORMATIONS.filter((formation) => formation.nom.toLowerCase().includes(recherche.toLowerCase()));
  }
}
