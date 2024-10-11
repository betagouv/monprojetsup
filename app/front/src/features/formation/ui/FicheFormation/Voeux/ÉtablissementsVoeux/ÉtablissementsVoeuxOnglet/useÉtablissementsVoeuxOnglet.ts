import { type UseÉtablissementsVoeuxOngletArgs } from "./ÉtablissementsVoeuxOnglet.interface";
import { useEffect, useMemo, useState } from "react";

const RAYONS = [10, 20, 50];

export default function useÉtablissementsVoeuxOnglet({ formation, codeCommune }: UseÉtablissementsVoeuxOngletArgs) {
  const NOMBRE_ÉTABLISSEMENTS_À_AFFICHER = 5;

  const [rayonSélectionné, setRayonSélectionné] = useState(RAYONS[0]);

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
    () =>
      [...établissements]
        .filter(({ distanceEnKm }) => distanceEnKm <= rayonSélectionné)
        .sort((a, b) => a.distanceEnKm - b.distanceEnKm),
    [rayonSélectionné, établissements],
  );

  return {
    nombreÉtablissementÀAfficher: NOMBRE_ÉTABLISSEMENTS_À_AFFICHER,
    nombreÉtablissementsDansLeRayon: établissementsÀAfficher.length,
    établissementsÀAfficher: établissementsÀAfficher.slice(0, NOMBRE_ÉTABLISSEMENTS_À_AFFICHER),
    rayons: RAYONS,
    rayonSélectionné,
    changerRayonSélectionné: setRayonSélectionné,
  };
}
