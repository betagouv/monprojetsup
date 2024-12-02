import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { Formation, Voeu } from "@/features/formation/domain/formation.interface";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import Fuse from "fuse.js";
import { useState } from "react";

export default function useRechercheVoeux() {
  const [voeuxSuggérés, setVoeuxSuggérés] = useState<Voeu[]>([]);
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFormationQueryOptions(formationAffichée.id));

  const rechercher = (recherche: string): Voeu[] => {
    if (!formation) return [];

    const fuse = new Fuse<Formation["voeux"][number]>(formation.voeux, {
      distance: 200,
      threshold: 0.2,
      keys: ["nom"],
    });

    const voeuxCorrespondants = fuse.search(recherche).map((correspondance) => correspondance.item);
    setVoeuxSuggérés(voeuxCorrespondants);
    return voeuxCorrespondants;
  };

  return {
    rechercher,
    voeuxSuggérés,
  };
}
