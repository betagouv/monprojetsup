import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import { récupérerFicheFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { rechercheVoeuxQueryOptions } from "@/features/formation/ui/Voeux/voeuxQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function useRechercheVoeux() {
  const [voeuRecherché, setVoeuRecherché] = useState<string>();
  const formationAffiché = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFicheFormationQueryOptions(formationAffiché.id));

  const { data: voeuxSuggérés, isFetching: rechercheEnCours } = useQuery(
    rechercheVoeuxQueryOptions(formation?.id ?? "", formation?.voeux ?? [], voeuRecherché),
  );

  return {
    rechercher: setVoeuRecherché,
    voeuxSuggérés,
    rechercheEnCours,
  };
}
