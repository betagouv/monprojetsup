import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export default function useSpécialité() {
  const { estSpécialitéFavoritePourÉlève } = useÉlève();
  const { mettreÀJourSpécialitésÉlève } = useÉlèveMutation();

  const spécialitéVersFavori = (spécialité: Spécialité): Favori => {
    return {
      id: spécialité.id,
      nom: spécialité.nom,
      estFavori: estSpécialitéFavoritePourÉlève(spécialité.id),
      callbackMettreÀJour: () => mettreÀJourSpécialitésÉlève([spécialité.id]),
    };
  };

  return {
    spécialitéVersFavori,
  };
}
