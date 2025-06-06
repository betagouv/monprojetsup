import { type FicheFormation } from "@/features/formation/domain/formation.interface";

export type ListeFormationsProps = {
  formations: FicheFormation[];
  affichéSurLaPage: "ficheFormation" | "favoris";
  estPersonnalisé: boolean;
};
