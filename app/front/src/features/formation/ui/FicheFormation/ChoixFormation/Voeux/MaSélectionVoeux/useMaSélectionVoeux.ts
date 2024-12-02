import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import useVoeu from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/useVoeu";
import { récupérerFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useMaSélectionVoeux() {
  const { élève } = useÉlève({});
  const { voeuVersFavori } = useVoeu();
  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFormationQueryOptions(formationAffichée.id));

  const favoris = useMemo(() => {
    const voeuxFavorisPourLaFormation =
      élève?.voeuxFavoris
        ?.map((voeu) => formation?.voeux.find(({ id }) => voeu.id === id))
        .filter((voeu) => voeu !== undefined) ?? [];

    return voeuxFavorisPourLaFormation.map(voeuVersFavori);
  }, [élève]);

  return {
    favoris,
  };
}
