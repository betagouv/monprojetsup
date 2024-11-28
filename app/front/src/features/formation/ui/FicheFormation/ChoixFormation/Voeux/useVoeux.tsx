import { type UseVoeuxArgs } from "./Voeux.interface";
import useLienParcoursupVoeu from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/useLienParcoursupVoeu.ts";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo } from "react";

export default function useVoeux({ formation }: UseVoeuxArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

  const { creerUrlParcoursup } = useLienParcoursupVoeu();

  const voeuxSélectionnés = useMemo(() => {
    if (!élève) return [];

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formation.id);

    const idsVoeuxSelectionnés = new Set(formationFavorite?.voeux);

    const voeuxFavoris = formation.voeux
      .filter((voeu) => idsVoeuxSelectionnés.has(voeu.id))
      .map((voeu) => ({
        id: voeu.id,
        nom: voeu.nom,
        urlParcoursup: creerUrlParcoursup(voeu.id),
      }));
    return voeuxFavoris ?? [];
  }, [creerUrlParcoursup, formation.id, formation.voeux, élève]);

  const mettreÀJourLesVoeux = (voeux: FormationFavorite["voeux"]) => {
    void mettreÀJourUneFormationFavorite(formation.id, {
      voeux: [...voeux],
    });
  };

  const mettreÀJourUnVoeu = (voeu: FormationFavorite["voeux"][number]) => {
    if (!élève) return;

    const voeux = new Set(voeuxSélectionnés.map(({ id }) => id));

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
