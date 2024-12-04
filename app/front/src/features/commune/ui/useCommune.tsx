import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { Commune } from "@/features/commune/domain/commune.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useCommune() {
  const { élève, mettreÀJourCommunesÉlève } = useÉlève({});

  const communeVersFavori = (commune: Commune): Favori => {
    const estFavorite =
      élève?.communesFavorites?.some((communeFavorite) => communeFavorite.codeInsee === commune.codeInsee) ?? false;

    return {
      id: commune.codeInsee,
      nom: commune.nom,
      estFavori: estFavorite,
      callbackMettreÀJour: () =>
        mettreÀJourCommunesÉlève([
          {
            nom: commune.nom,
            codeInsee: commune.codeInsee,
            latitude: commune.latitude,
            longitude: commune.longitude,
          },
        ]),
    };
  };

  return {
    communeVersFavori,
  };
}
