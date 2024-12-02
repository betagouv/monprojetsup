import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { TitreProps } from "@/components/Titre/Titre.interface";

export type MaSélectionFavorisProps = {
  favoris: Favori[];
  niveauDeTitre: TitreProps["niveauDeTitre"];
  messageAucun: string;
};
