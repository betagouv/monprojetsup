/* eslint-disable sonarjs/rules-of-hooks */
import { type UseÉtablissementsVoeuxArgs } from "./ÉtablissementsVoeux.interface";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo } from "react";

export default function useÉtablissementsVoeux({ formation }: UseÉtablissementsVoeuxArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

  const voeuxSélectionnés = useMemo(() => {
    if (!élève) return [];

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formation.id);

    return formationFavorite?.voeux ?? [];
  }, [formation.id, élève]);

  const mettreÀJourLesVoeux = (voeux: FormationFavorite["voeux"]) => {
    void mettreÀJourUneFormationFavorite(formation.id, {
      voeux: [...voeux],
    });
  };

  const mettreÀJourUnVoeu = (voeu: FormationFavorite["voeux"][number]) => {
    if (!élève) return;

    const voeux = new Set(voeuxSélectionnés);

    if (voeux.has(voeu)) {
      voeux.delete(voeu);
    } else {
      voeux.add(voeu);
    }

    mettreÀJourLesVoeux([...voeux]);
  };

  return {
    communesFavorites: élève?.communesFavorites,
    mettreÀJourUnVoeu,
    mettreÀJourLesVoeux,
    voeuxSélectionnés,
    key: JSON.stringify(élève?.formationsFavorites),
  };
}
