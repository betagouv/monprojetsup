import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { Formation } from "@/features/formation/domain/formation.interface";

const estUnIdDeFormation = (id: string): boolean => {
  return /^(?:fl|fr)\d+/iu.test(id);
};

export default function useFormation() {
  const { estFormationFavoritePourÉlève } = useÉlève();
  const { mettreÀJourFormationsÉlève } = useÉlèveMutation();

  const formationVersFavori = (formation: Formation): Favori => {
    return {
      id: formation.id,
      nom: formation.nom,
      estFavori: estFormationFavoritePourÉlève(formation.id),
      callbackMettreÀJour: () => mettreÀJourFormationsÉlève([formation.id]),
    };
  };

  return {
    estUnIdDeFormation,
    formationVersFavori,
  };
}
