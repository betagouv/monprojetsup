import { rechercherMétiersQueryOptions } from "@/features/métier/ui/métierQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function useRechercheMétiers() {
  const [métierRecherché, setMétierRecherché] = useState<string>();

  const { data: métiersSuggérés, isFetching: rechercheEnCours } = useQuery(
    rechercherMétiersQueryOptions(métierRecherché),
  );

  return {
    rechercher: setMétierRecherché,
    métiersSuggérés,
    rechercheEnCours,
  };
}
