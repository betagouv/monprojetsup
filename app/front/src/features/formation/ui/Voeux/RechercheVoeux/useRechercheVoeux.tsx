import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { rechercheVoeuxQueryOptions } from "@/features/formation/ui/Voeux/voeuxQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function useRechercheVoeux() {
  const [voeuRecherché, setVoeuRecherché] = useState<string>();
  const formationAffiché = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFormationQueryOptions(formationAffiché.id));

  const { data: voeuxSuggérés, isFetching: rechercheEnCours } = useQuery(
    rechercheVoeuxQueryOptions(formation?.voeux ?? [], voeuRecherché),
  );

  return {
    rechercher: setVoeuRecherché,
    voeuxSuggérés,
    rechercheEnCours,
  };
}
