import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { Spécialité } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useSpécialité() {
  const { élève, mettreÀJourSpécialitésÉlève } = useÉlève({});

  const spécialitéVersFavori = (spécialité: Spécialité): Favori => {
    const estFavorite = élève?.spécialités?.some((spécialitéFavorite) => spécialitéFavorite === spécialité.id) ?? false;

    return {
      id: spécialité.id,
      nom: spécialité.nom,
      estFavori: estFavorite,
      callbackMettreÀJour: () => mettreÀJourSpécialitésÉlève([spécialité.id]),
    };
  };

  return {
    spécialitéVersFavori,
  };
}
