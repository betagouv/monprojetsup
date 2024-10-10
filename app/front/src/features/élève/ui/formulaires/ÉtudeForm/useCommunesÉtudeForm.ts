import { type UseCommunesÉtudeFormArgs } from "./ÉtudeForm.interface";
import { type SélecteurMultipleOption } from "@/components/SélecteurMultiple/SélecteurMultiple.interface";
import { type Commune } from "@/features/commune/domain/commune.interface";
import { rechercheCommunesQueryOptions } from "@/features/commune/ui/communeQueries";
import { type CommuneFavorite } from "@/features/élève/domain/élève.interface";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function useCommunesÉtudeForm({ getValues, setValue }: UseCommunesÉtudeFormArgs) {
  const [rechercheCommune, setRechercheCommune] = useState<string>();

  const communeVersOptionCommune = useCallback((commune: Commune | CommuneFavorite) => {
    return {
      valeur: JSON.stringify(commune),
      label: commune.nom,
    };
  }, []);

  const communesSélectionnéesParDéfaut = useMemo(() => getValues("communesFavorites"), [getValues]);

  const {
    data: communes,
    refetch: rechercherCommunes,
    isFetching: rechercheCommunesEnCours,
  } = useQuery(rechercheCommunesQueryOptions(rechercheCommune));

  useEffect(() => {
    if (rechercheCommune && rechercheCommune.length >= 3) {
      rechercherCommunes();
    }
  }, [rechercheCommune, rechercherCommunes]);

  useEffect(() => setValue("communesFavorites", []), [setValue]);

  const auChangementDesCommunesSélectionnées = (communesSélectionnées: SélecteurMultipleOption[]) => {
    setValue(
      "communesFavorites",
      communesSélectionnées.map((commune) => JSON.parse(commune.valeur)),
    );
  };

  return {
    communesSuggérées: communes?.map(communeVersOptionCommune) ?? [],
    communesSélectionnéesParDéfaut: communesSélectionnéesParDéfaut?.map(communeVersOptionCommune) ?? [],
    rechercheCommunesEnCours,
    auChangementDesCommunesSélectionnées,
    àLaRechercheDUneCommune: setRechercheCommune,
  };
}
