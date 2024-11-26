/* eslint-disable sonarjs/rules-of-hooks */
import { type UseÉtablissementsVoeuxArgs } from "./ÉtablissementsVoeux.interface";
import useLienParcoursupVoeu from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/useLienParcoursupVoeu.ts";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useMemo } from "react";

export default function useÉtablissementsVoeux({ formation }: UseÉtablissementsVoeuxArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});

  const { creerUrlParcoursup } = useLienParcoursupVoeu();

  const voeuxSélectionnés = useMemo(() => {
    if (!élève) return [];

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formation.id);

    const idsVoeuxSelectionnés = new Set(formationFavorite?.voeux);

    const voeuxFavoris = formation.établissements
      .filter((etablissement) => idsVoeuxSelectionnés.has(etablissement.id))
      .map((etablissement) => ({
        id: etablissement.id,
        nom: etablissement.nom,
        urlParcoursup: creerUrlParcoursup(etablissement.id),
      }));
    return voeuxFavoris ?? [];
  }, [creerUrlParcoursup, formation.id, formation.établissements, élève]);

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
