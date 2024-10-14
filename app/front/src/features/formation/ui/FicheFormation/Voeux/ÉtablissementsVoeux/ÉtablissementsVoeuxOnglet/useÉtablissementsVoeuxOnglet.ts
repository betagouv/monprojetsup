/* eslint-disable sonarjs/rules-of-hooks */
import { type UseÉtablissementsVoeuxOngletArgs } from "./ÉtablissementsVoeuxOnglet.interface";
import { constantes } from "@/configuration/constantes";
import { useEffect, useMemo, useState } from "react";

const rayons = constantes.FICHE_FORMATION.RAYONS_RECHERCHE_ÉTABLISSEMENTS;
export default function useÉtablissementsVoeuxOnglet({ formation, codeCommune }: UseÉtablissementsVoeuxOngletArgs) {
  const [rayonSélectionné, setRayonSélectionné] = useState<(typeof rayons)[number]>(rayons[0]);

  const établissements = useMemo(() => {
    return (
      formation.établissementsParCommuneFavorites.find((élément) => élément.commune.code === codeCommune)
        ?.établissements ?? []
    );
  }, [codeCommune, formation.établissementsParCommuneFavorites]);

  const établissementsParRayon = useMemo(
    () =>
      rayons.map((rayon) => ({
        rayon,
        établissements: établissements.filter(({ distanceEnKm }) => distanceEnKm <= rayon),
      })),
    [établissements],
  );

  useEffect(() => {
    const rayonParDéfaut = établissementsParRayon.find(({ établissements: it }) => it.length)?.rayon ?? rayons[2];
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
    nombreÉtablissementÀAfficher: constantes.FICHE_FORMATION.NB_MAX_ÉTABLISSEMENTS,
    nombreÉtablissementsDansLeRayon: établissementsÀAfficher.length,
    établissementsÀAfficher: établissementsÀAfficher.slice(0, constantes.FICHE_FORMATION.NB_MAX_ÉTABLISSEMENTS),
    rayons,
    rayonSélectionné,
    changerRayonSélectionné: setRayonSélectionné,
  };
}
