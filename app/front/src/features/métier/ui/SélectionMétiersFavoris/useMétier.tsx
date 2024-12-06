import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { Métier } from "@/features/métier/domain/métier.interface.ts";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";

export default function useMétier() {
  const { élève, mettreÀJourMétiersFavorisÉlève } = useÉlève();

  const métierVersFavori = (métier: Métier): Favori => {
    const estfavori = élève?.métiersFavoris?.includes(métier.id) ?? false;

    return {
      id: métier.id,
      nom: métier.nom,
      estFavori: estfavori,
      callbackMettreÀJour: () => mettreÀJourMétiersFavorisÉlève([métier.id]),
    };
  };

  return {
    métierVersFavori,
  };
}
