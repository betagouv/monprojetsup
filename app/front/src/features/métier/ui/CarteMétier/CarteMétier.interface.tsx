import { type Métier } from "@/features/métier/domain/métier.interface";

export type CarteMétierProps = {
  id: Métier["id"];
  titre: Métier["nom"];
  formations: Métier["formations"];
  sélectionnée?: boolean;
};
