import { rechercheCommunesQueryOptions } from "@/features/commune/ui/communeQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function useRechercheCommunes() {
  const [communeRecherchée, setCommuneRecherchée] = useState<string>();

  const { data: communesSuggérées, isFetching: rechercheEnCours } = useQuery(
    rechercheCommunesQueryOptions(communeRecherchée),
  );

  return {
    rechercher: setCommuneRecherchée,
    communesSuggérées,
    rechercheEnCours,
  };
}
