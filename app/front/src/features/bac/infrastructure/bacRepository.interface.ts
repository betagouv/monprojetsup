import { type Bac, type Spécialité } from "@/features/bac/domain/bac.interface";

export type BacRepository = {
  récupérerTous: () => Promise<Bac[] | undefined>;
  rechercherSpécialitésDUnBac: (bacId: Bac["id"], recherche?: string) => Promise<Spécialité[] | undefined>;
  récupérerSpécialités: (spécialitéIds?: Array<Spécialité["id"]>) => Promise<Spécialité[] | undefined>;
};
