import { UseRechercheSpécialitésArgs } from "./RechercheSpécialités.interface";
import { rechercheSpécialitésQueryOptions } from "@/features/élève/ui/formulaires/ScolaritéForm/Spécialités/spécialitéQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function useRechercheSpécialités({ spécialitésBac }: UseRechercheSpécialitésArgs) {
  const [spécialitéRecherchée, setSpécialitéRecherchée] = useState<string>();

  const { data: spécialitésSuggérées, isFetching: rechercheEnCours } = useQuery(
    rechercheSpécialitésQueryOptions(spécialitésBac, spécialitéRecherchée),
  );

  return {
    rechercher: setSpécialitéRecherchée,
    spécialitésSuggérées,
    rechercheEnCours,
  };
}
