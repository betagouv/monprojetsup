import { type UseVoeuxOngletArgs } from "./VoeuxOnglet.interface";
import { constantes } from "@/configuration/constantes";
import useLienParcoursupVoeu from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/useLienParcoursupVoeu.ts";
import { useEffect, useMemo, useState } from "react";

const rayons = constantes.FICHE_FORMATION.RAYONS_RECHERCHE_VOEUX;
export default function useVoeuxOnglet({ formation, codeCommune }: UseVoeuxOngletArgs) {
  const [rayonSélectionné, setRayonSélectionné] = useState<(typeof rayons)[number]>(rayons[0]);
  const [nombreVoeuÀAfficher, setNombreVoeuÀAfficher] = useState<number>(constantes.VOEUX.NB_VOEUX_PAR_PAGE);

  const voeux = useMemo(() => {
    return formation.voeuxParCommuneFavorites.find((élément) => élément.commune.code === codeCommune)?.voeux ?? [];
  }, [codeCommune, formation.voeuxParCommuneFavorites]);

  const voeuxParRayon = useMemo(
    () =>
      rayons.map((rayon) => ({
        rayon,
        voeux: voeux.filter(({ distanceEnKm }) => distanceEnKm <= rayon),
      })),
    [voeux],
  );

  useEffect(() => {
    const rayonParDéfaut = voeuxParRayon.find(({ voeux: it }) => it.length)?.rayon ?? rayons[2];
    setRayonSélectionné(rayonParDéfaut);
  }, [voeuxParRayon]);

  const { creerUrlParcoursup } = useLienParcoursupVoeu();
  const voeuxÀAfficher = useMemo(
    () =>
      [...voeux]
        .filter(({ distanceEnKm }) => distanceEnKm <= rayonSélectionné)
        .sort((a, b) => a.distanceEnKm - b.distanceEnKm)
        .map((voeu) => ({
          id: voeu.id,
          nom: voeu.nom,
          urlParcoursup: creerUrlParcoursup(voeu.id),
        })),
    [rayonSélectionné, voeux],
  );

  const afficherPlusDeRésultats = () => {
    setNombreVoeuÀAfficher(Math.min(nombreVoeuÀAfficher + constantes.VOEUX.NB_VOEUX_PAR_PAGE, voeuxÀAfficher.length));
  };

  return {
    nombreVoeuÀAfficher,
    nombreVoeuxDansLeRayon: voeuxÀAfficher.length,
    voeuxÀAfficher: voeuxÀAfficher.slice(0, nombreVoeuÀAfficher),
    afficherPlusDeRésultats,
    rayons,
    rayonSélectionné,
    changerRayonSélectionné: setRayonSélectionné,
  };
}
