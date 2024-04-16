import { type Bac, type Spécialité } from "@/features/bac/domain/bac.interface";

export type BacRepository = {
  récupérerTous: () => Promise<Bac[] | undefined>;
  récupérerSpécialités: (bacId: Bac["id"]) => Promise<Spécialité[] | undefined>;
};
