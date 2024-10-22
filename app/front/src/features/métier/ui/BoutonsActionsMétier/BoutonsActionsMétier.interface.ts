import { type BoutonProps } from "@/components/Bouton/Bouton.interface";
import { MétierSansFormationsAssociées } from "@/features/métier/domain/métier.interface";

export type useBoutonsActionsMétierArgs = {
  métier: MétierSansFormationsAssociées;
};

export type BoutonsActionsFicheMétierProps = {
  métier: MétierSansFormationsAssociées;
  taille: BoutonProps["taille"];
};
