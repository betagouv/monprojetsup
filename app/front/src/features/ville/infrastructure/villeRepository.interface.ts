import { type Ville } from "@/features/ville/domain/ville.interface";

export type VilleRepository = {
  rechercher: (recherche: string) => Promise<Ville[] | undefined>;
};
