import { rechercherFormationsQueryOptions } from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function useRechercheFormations() {
  const [formationRecherchée, setFormationRecherchée] = useState<string>();

  const { data: formationsSuggérées, isFetching: rechercheEnCours } = useQuery(
    rechercherFormationsQueryOptions(formationRecherchée),
  );

  return {
    rechercher: setFormationRecherchée,
    formationsSuggérées,
    rechercheEnCours,
  };
}
