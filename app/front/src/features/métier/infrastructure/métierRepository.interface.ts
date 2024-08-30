import { type Métier } from "@/features/métier/domain/métier.interface";

export type MétierRepository = {
  récupérerPlusieurs: (métierIds: Array<Métier["id"]>) => Promise<Métier[] | undefined>;
  rechercher: (recherche: string) => Promise<Métier[] | undefined>;
};
