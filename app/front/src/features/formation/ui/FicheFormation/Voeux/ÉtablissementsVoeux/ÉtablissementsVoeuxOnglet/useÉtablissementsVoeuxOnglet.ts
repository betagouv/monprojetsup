/* eslint-disable sonarjs/pluginRules-of-hooks */
/* eslint-disable react-hooks/rules-of-hooks */
import { type useÉtablissementsVoeuxOngletArgs } from "./ÉtablissementsVoeuxOnglet.interface";
import { type FormationFavorite } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useEffect, useMemo, useState } from "react";

const RAYONS = [10, 20, 50];

export default function useÉtablissementsVoeuxOnglet({
  établissements,
  formationId,
}: useÉtablissementsVoeuxOngletArgs) {
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});
  const [rayonSélectionné, setRayonSélectionné] = useState(RAYONS[0]);

  const établissementsParRayon = useMemo(
    () =>
      RAYONS.map((rayon) => ({
        rayon,
        établissements: établissements.filter(({ distanceEnKm }) => distanceEnKm <= rayon),
      })),
    [établissements],
  );

  useEffect(() => {
    const rayonParDéfaut = établissementsParRayon.find(({ établissements: it }) => it.length)?.rayon || RAYONS[2];
    setRayonSélectionné(rayonParDéfaut);
  }, [établissementsParRayon]);

  const établissementsÀAfficher = useMemo(
    () => établissements.filter(({ distanceEnKm }) => distanceEnKm <= rayonSélectionné),
    [rayonSélectionné, établissements],
  );

  const voeuxSélectionnés = useMemo(() => {
    if (!élève) return [];

    const formationFavorite = élève.formationsFavorites?.find(({ id }) => id === formationId);

    return formationFavorite?.voeux ?? [];
  }, [formationId, élève]);

  const mettreÀJourVoeux = (voeu: FormationFavorite["voeux"][number]) => {
    if (!élève) return;

    const voeux = new Set(voeuxSélectionnés);

    if (voeux.has(voeu)) {
      voeux.delete(voeu);
    } else {
      voeux.add(voeu);
    }

    mettreÀJourUneFormationFavorite(formationId, {
      voeux: [...voeux],
    });
  };

  return {
    établissementsÀAfficher,
    rayons: RAYONS,
    rayonSélectionné,
    changerRayonSélectionné: setRayonSélectionné,
    mettreÀJourVoeux,
    voeuxSélectionnés,
    key: JSON.stringify(élève?.formationsFavorites),
  };
}
