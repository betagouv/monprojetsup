import { type Formation } from "@/features/formation/domain/formation.interface";
import { type Métier } from "@/features/métier/domain/métier.interface";

export type BarreLatéraleFavorisProps = {
  métiers?: Métier[];
  formations?: Formation[];
};

export type UseBarreLatéraleFavorisArgs = {
  métiers?: BarreLatéraleFavorisProps["métiers"];
  formations?: BarreLatéraleFavorisProps["formations"];
};
