import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useMaSélectionVoeux() {
  const { élève } = useÉlève({});
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFormationQueryOptions(formationAffichée.id));

  const voeuxSélectionnés = useMemo(
    () =>
      élève?.voeuxFavoris
        ?.map((voeu) => formation?.voeux.find(({ id }) => voeu.id === id))
        .filter((voeu) => voeu !== undefined) ?? [],
    [élève],
  );

  return {
    voeuxSélectionnés,
  };
}
