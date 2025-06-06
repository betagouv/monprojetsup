import VoeuxOngletToutesLesCommunes from "./VoeuxOngletToutesLesCommunes/VoeuxOngletToutesLesCommunes";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { récupérerFicheFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import VoeuxOngletUneCommune from "@/features/formation/ui/Voeux/VoeuxOngletUneCommune/VoeuxOngletUneCommune";
import { trierTableauDObjetsParOrdreAlphabétique } from "@/utils/array";
import { useQuery } from "@tanstack/react-query";

export default function useVoeux() {
  const { élève } = useÉlève();
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFicheFormationQueryOptions(formationAffichée.id));

  const ongletsParCommuneFavorite = trierTableauDObjetsParOrdreAlphabétique(élève?.communesFavorites ?? [], "nom").map(
    (communeFavorite) => ({
      label: communeFavorite.nom,
      content: <VoeuxOngletUneCommune codeCommune={communeFavorite.codeInsee} />,
    }),
  );

  const ongletToutesLesCommunes = {
    label: i18n.PAGE_FORMATION.CHOIX.VOEUX.TOUTES_LES_COMMUNES.TITRE_ONGLET,
    content: <VoeuxOngletToutesLesCommunes />,
  };

  return {
    ongletToutesLesCommunes,
    ongletsParCommuneFavorite,
    lienParcoursup: formation?.lienParcoursSup,
  };
}
