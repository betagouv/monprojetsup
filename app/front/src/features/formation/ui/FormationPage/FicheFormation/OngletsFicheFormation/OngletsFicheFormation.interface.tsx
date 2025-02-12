import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type UseOngletFicheFormationArgs = {
  formation: FicheFormation;
  onglets: string[];
};

export type OngletsFicheFormationProps = {
  formation: FicheFormation;
};
