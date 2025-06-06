import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { Métier } from "@/features/métier/domain/métier.interface";

const estUnIdDeMétier = (id: string): boolean => {
  return /^MET\.\d+/iu.test(id);
};

export default function useMétier() {
  const { estMétierFavoriPourÉlève } = useÉlève();
  const { mettreÀJourMétiersÉlève } = useÉlèveMutation();

  const métierVersFavori = (métier: Métier): Favori => {
    return {
      id: métier.id,
      nom: métier.nom,
      estFavori: estMétierFavoriPourÉlève(métier.id),
      callbackMettreÀJour: () => mettreÀJourMétiersÉlève([métier.id]),
    };
  };

  return {
    estUnIdDeMétier,
    métierVersFavori,
  };
}
