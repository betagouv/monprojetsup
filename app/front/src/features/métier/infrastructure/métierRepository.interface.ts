import { type Métier } from "@/features/métier/domain/métier.interface";

export type MétierRepository = {
  récupérerTous: () => Promise<Métier[] | undefined>;
};
