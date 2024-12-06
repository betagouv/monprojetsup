import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { Commune } from "@/features/commune/domain/commune.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";

export default function useCommune() {
  const { estCommuneFavoritePourÉlève } = useÉlève();
  const { mettreÀJourCommunesÉlève } = useÉlèveMutation();

  const communeVersFavori = (commune: Commune): Favori => {
    return {
      id: commune.codeInsee,
      nom: commune.nom,
      estFavori: estCommuneFavoritePourÉlève(commune.codeInsee),
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
