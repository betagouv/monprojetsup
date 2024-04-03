import { type Élève } from "@/features/élève/domain/élève.interface";

export type ÉlèveRepository = {
  récupérer: () => Promise<Élève | undefined>;
  mettreÀJour: (données: Partial<Omit<Élève, "id">>) => Promise<Élève | undefined>;
  créer: () => Promise<Élève>;
};
