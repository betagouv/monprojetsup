import { type CatégorieCentresIntêrets } from "@/features/centreIntêret/domain/centreIntêret.interface";

export type CentreIntêretRepository = {
  récupérerTousGroupésParCatégorie: () => Promise<CatégorieCentresIntêrets[] | undefined>;
};
