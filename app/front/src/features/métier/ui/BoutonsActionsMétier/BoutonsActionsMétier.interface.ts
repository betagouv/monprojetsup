import { type BoutonProps } from "@/components/Bouton/Bouton.interface";
import { type Métier } from "@/features/métier/domain/métier.interface";

export type useBoutonsActionsMétierArgs = {
  métier: Métier;
};

export type BoutonsActionsModalMétierProps = {
  métier: Métier;
  taille: BoutonProps["taille"];
  ariaControls?: string;
};

export type BoutonsActionsFicheMétierProps = {
  métier: Métier;
  taille: BoutonProps["taille"];
};
