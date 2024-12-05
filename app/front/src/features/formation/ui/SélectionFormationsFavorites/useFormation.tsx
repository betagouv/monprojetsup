import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { Formation } from "@/features/formation/domain/formation.interface.ts";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useFormation() {
  const { élève, mettreÀJourFormationsFavoritesÉlève } = useÉlève({});

  const formationVersFavori = (formation: Formation): Favori => {
    const estFavorite =
      élève?.formationsFavorites?.some((formationFavorite) => formationFavorite.id === formation.id) ?? false;

    return {
      id: formation.id,
      nom: formation.nom,
      estFavori: estFavorite,
      callbackMettreÀJour: () => mettreÀJourFormationsFavoritesÉlève([formation.id]),
    };
  };

  return {
    formationVersFavori,
  };
}
