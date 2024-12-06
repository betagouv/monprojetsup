import { type useBoutonsActionsMétierArgs } from "./BoutonsActionsMétier.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";

export default function useBoutonsActionsMétier({ métier }: useBoutonsActionsMétierArgs) {
  const { mettreÀJourMétiersÉlève } = useÉlèveMutation();
  const { estMétierFavoriPourÉlève } = useÉlève();

  return {
    estFavori: estMétierFavoriPourÉlève(métier.id),
    mettreÀJourMétiersÉlève,
  };
}
