import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";

export type ListeDeFavorisProps = {
  favoris: Favori[];
  listeDeSuggestions?: boolean;
  nombreFavorisAffichésParDéfaut?: number;
  boutonMPSVisible?: boolean | undefined;
};

export type UseListeDeFavorisArgs = {
  favoris: ListeDeFavorisProps["favoris"];
  nombreFavorisAffichésParDéfaut: NonNullable<ListeDeFavorisProps["nombreFavorisAffichésParDéfaut"]>;
};
