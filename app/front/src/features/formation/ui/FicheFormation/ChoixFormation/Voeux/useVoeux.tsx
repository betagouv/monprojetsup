import VoeuxOngletToutesLesCommunes from "./VoeuxOngletToutesLesCommunes/VoeuxOngletToutesLesCommunes";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { i18n } from "@/configuration/i18n/i18n";
import VoeuxOngletUneCommune from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/VoeuxOngletUneCommune/VoeuxOngletUneCommune.tsx";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { trierTableauDObjetsParOrdreAlphabétique } from "@/utils/array";
import { useQuery } from "@tanstack/react-query";

export default function useVoeux() {
  const { data: élève } = useQuery(élèveQueryOptions);
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFormationQueryOptions(formationAffichée.id));

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
