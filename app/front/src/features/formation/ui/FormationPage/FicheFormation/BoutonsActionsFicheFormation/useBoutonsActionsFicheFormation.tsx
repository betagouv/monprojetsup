import { type useBoutonsActionsFicheFormationArgs } from "./BoutonsActionsFicheFormation.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";

export default function useBoutonsActionsFicheFormation({ formation }: useBoutonsActionsFicheFormationArgs) {
  const { mettreÀJourFormationsÉlève, mettreÀJourFormationsMasquéesÉlève } = useÉlèveMutation();
  const { estFormationFavoritePourÉlève, estFormationMasquéePourÉlève } = useÉlève();

  return {
    estFavorite: estFormationFavoritePourÉlève(formation.id),
    estMasquée: estFormationMasquéePourÉlève(formation.id),
    mettreÀJourFormationsÉlève,
    mettreÀJourFormationsMasquéesÉlève,
  };
}
