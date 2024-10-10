import { type UseÉtablissementsVoeuxOngletArgs } from "./ÉtablissementsVoeuxOnglet.interface";
import { useEffect, useMemo, useState } from "react";

const RAYONS = [10, 20, 50];

export default function useÉtablissementsVoeuxOnglet({ formation, codeCommune }: UseÉtablissementsVoeuxOngletArgs) {
  const [rayonSélectionné, setRayonSélectionné] = useState(RAYONS[0]);
  const [afficherTousLesÉtablissements, setAfficherTousLesÉtablissements] = useState(false);

  const établissements = useMemo(() => {
    return (
      formation.établissementsParCommuneFavorites.find((élément) => élément.commune.code === codeCommune)
        ?.établissements ?? []
    );
  }, [codeCommune, formation.établissementsParCommuneFavorites]);

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

  const changerRayonSélectionné = (rayon: number) => {
    setRayonSélectionné(rayon);
    setAfficherTousLesÉtablissements(false);
  };

  return {
    établissementsÀAfficher,
    rayons: RAYONS,
    rayonSélectionné,
    changerRayonSélectionné,
    afficherTousLesÉtablissements,
    setAfficherTousLesÉtablissements,
  };
}
