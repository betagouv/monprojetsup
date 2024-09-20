import {
  actionsListeEtAperçuStore,
  catégorieAffichéeListeEtAperçuStore,
} from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { type ListeEtAperçuStoreState } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu.interface";

export default function useBarreLatéraleFavoris() {
  const catégorieAffichée = catégorieAffichéeListeEtAperçuStore();
  const { réinitialiserÉlémentAffiché, changerCatégorieAffichée } = actionsListeEtAperçuStore();

  const auChangementDeCatégorie = (catégorieSélectionnée: ListeEtAperçuStoreState["catégorieAffichée"]) => {
    réinitialiserÉlémentAffiché();
    changerCatégorieAffichée(catégorieSélectionnée);
  };

  return {
    catégorieAffichée,
    auChangementDeCatégorie,
  };
}
