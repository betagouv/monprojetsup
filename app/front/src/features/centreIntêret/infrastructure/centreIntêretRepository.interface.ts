import { type CatégorieCentreIntêret } from "@/features/centreIntêret/domain/centreIntêret.interface";

export type CentreIntêretRepository = {
  récupérer: () => Promise<CatégorieCentreIntêret[] | undefined>;
};
