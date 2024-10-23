import { UseMétiersFavorisMétiersFormArgs } from "./MétiersForm.interface";
import { SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { constantes } from "@/configuration/constantes";
import { Métier } from "@/features/métier/domain/métier.interface";
import { rechercherMétiersQueryOptions, récupérerMétiersQueryOptions } from "@/features/métier/ui/métierQueries";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useState } from "react";

export default function useMétiersFavorisMétiersForm({ setValue, getValues }: UseMétiersFavorisMétiersFormArgs) {
  const [rechercheMétier, setRechercheMétier] = useState<string>();

  const métierVersOptionMétierFavori = useCallback((métier: Métier): SélecteurMultipleOption => {
    return {
      valeur: métier.id,
      label: métier.nom,
    };
  }, []);

  const { data: métiers, isFetching: rechercheEnCours } = useQuery({
    ...rechercherMétiersQueryOptions(rechercheMétier),
    enabled: Boolean(rechercheMétier && rechercheMétier?.length >= constantes.MÉTIERS.NB_CARACTÈRES_MIN_RECHERCHE),
  });

  const { data: métiersSélectionnésParDéfaut } = useQuery(
    récupérerMétiersQueryOptions(getValues(constantes.MÉTIERS.CHAMP_MÉTIERS_FAVORIS) ?? []),
  );

  const auChangementDesMétiersSélectionnés = (métiersSélectionnés: SélecteurMultipleOption[]) => {
    setValue(
      constantes.MÉTIERS.CHAMP_MÉTIERS_FAVORIS,
      métiersSélectionnés.map((métier) => métier.valeur),
    );
  };

  return {
    métiersSuggérés: métiers?.map(métierVersOptionMétierFavori) ?? [],
    métiersSélectionnésParDéfaut: métiersSélectionnésParDéfaut?.map(métierVersOptionMétierFavori),
    rechercheEnCours,
    auChangementDesMétiersSélectionnés,
    àLaRechercheDUnMétier: setRechercheMétier,
  };
}
