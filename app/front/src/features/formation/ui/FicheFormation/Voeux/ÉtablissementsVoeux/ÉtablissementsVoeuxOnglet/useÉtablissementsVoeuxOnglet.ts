/* eslint-disable sonarjs/rules-of-hooks */
import { type UseÉtablissementsVoeuxOngletArgs } from "./ÉtablissementsVoeuxOnglet.interface";
import { constantes } from "@/configuration/constantes";
import useLienParcoursupVoeu from "@/features/formation/ui/FicheFormation/Voeux/ÉtablissementsVoeux/ÉtablissementsVoeuxOnglet/useLienParcoursupVoeu.ts";
import { useEffect, useMemo, useState } from "react";

const rayons = constantes.FICHE_FORMATION.RAYONS_RECHERCHE_ÉTABLISSEMENTS;
export default function useÉtablissementsVoeuxOnglet({ formation, codeCommune }: UseÉtablissementsVoeuxOngletArgs) {
  const [rayonSélectionné, setRayonSélectionné] = useState<(typeof rayons)[number]>(rayons[0]);
  const [nombreÉtablissementÀAfficher, setNombreÉtablissementÀAfficher] = useState<number>(
    constantes.ÉTABLISSEMENTS.PAGINATION_ÉTABLISSEMENTS,
  );

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

  const { creerUrlParcoursup } = useLienParcoursupVoeu();
  const établissementsÀAfficher = useMemo(
    () =>
      [...établissements]
        .filter(({ distanceEnKm }) => distanceEnKm <= rayonSélectionné)
        .sort((a, b) => a.distanceEnKm - b.distanceEnKm)
        .map((établissement) => ({
          id: établissement.id,
          nom: établissement.nom,
          urlParcoursup: creerUrlParcoursup(établissement.id),
        })),
    [rayonSélectionné, établissements],
  );

  const afficherPlusDeRésultats = () => {
    setNombreÉtablissementÀAfficher(
      Math.min(
        nombreÉtablissementÀAfficher + constantes.ÉTABLISSEMENTS.PAGINATION_ÉTABLISSEMENTS,
        établissementsÀAfficher.length,
      ),
    );
  };

  return {
    nombreÉtablissementÀAfficher,
    nombreÉtablissementsDansLeRayon: établissementsÀAfficher.length,
    établissementsÀAfficher: établissementsÀAfficher.slice(0, nombreÉtablissementÀAfficher),
    afficherPlusDeRésultats,
    rayons,
    rayonSélectionné,
    changerRayonSélectionné: setRayonSélectionné,
  };
}
