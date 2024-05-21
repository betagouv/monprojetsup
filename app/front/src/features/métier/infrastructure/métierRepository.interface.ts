import { type MétierAperçu } from "@/features/métier/domain/métier.interface";

export type MétierRepository = {
  rechercher: (recherche: string) => Promise<MétierAperçu[] | undefined>;
  récupérerAperçus: (métierIds: Array<MétierAperçu["id"]>) => Promise<MétierAperçu[] | undefined>;
};
