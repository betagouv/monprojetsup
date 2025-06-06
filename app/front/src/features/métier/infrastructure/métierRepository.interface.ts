import { type Métier } from "@/features/métier/domain/métier.interface";

export type MétierRepository = {
  récupérer: (métierId: Métier["id"]) => Promise<Métier | Error>;
  récupérerPlusieurs: (métierIds: Array<Métier["id"]>) => Promise<Métier[] | Error>;
  rechercher: (recherche: string) => Promise<Métier[] | Error>;
};
