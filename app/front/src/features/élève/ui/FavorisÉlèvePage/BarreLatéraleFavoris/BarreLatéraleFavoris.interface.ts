import { type FicheFormation } from "@/features/formation/domain/formation.interface";
import { type Métier } from "@/features/métier/domain/métier.interface";

export type BarreLatéraleFavorisProps = {
  métiers?: Métier[];
  formations?: FicheFormation[];
};

export type UseBarreLatéraleFavorisArgs = {
  métiers?: BarreLatéraleFavorisProps["métiers"];
  formations?: BarreLatéraleFavorisProps["formations"];
};
