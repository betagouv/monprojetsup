import { type UseVoeuxOngletUneCommuneArgs } from "./VoeuxOngletUneCommune.interface.tsx";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore.ts";
import { constantes } from "@/configuration/constantes";
import { récupérerFicheFormationQueryOptions } from "@/features/formation/ui/formationQueries";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo, useState } from "react";

const rayons = constantes.VOEUX.RAYONS_RECHERCHE;
export default function useVoeuxOngletUneCommune({ codeCommune }: UseVoeuxOngletUneCommuneArgs) {
  const [rayonSélectionné, setRayonSélectionné] = useState<(typeof rayons)[number]>(rayons[0]);

  const formationAffichée = élémentAffichéListeEtAperçuStore();
  const { data: formation } = useQuery(récupérerFicheFormationQueryOptions(formationAffichée.id));

  const voeuxÀProximitéDeLaCommune = useMemo(() => {
    return formation?.voeuxParCommuneFavorites.find((élément) => élément.commune.code === codeCommune)?.voeux ?? [];
  }, [codeCommune, formation]);

  const voeuxParRayon = useMemo(
    () =>
      rayons.map((rayon) => ({
        rayon,
        voeux: voeuxÀProximitéDeLaCommune.filter(({ distanceEnKm }) => distanceEnKm <= rayon),
      })),
    [voeuxÀProximitéDeLaCommune],
  );

  useEffect(() => {
    const rayonParDéfaut = voeuxParRayon.find(({ voeux: it }) => it.length)?.rayon ?? rayons[2];
    setRayonSélectionné(rayonParDéfaut);
  }, [voeuxParRayon]);

  const voeuxTriés = useMemo(
    () =>
      [...voeuxÀProximitéDeLaCommune]
        .filter(({ distanceEnKm }) => distanceEnKm <= rayonSélectionné)
        .sort((a, b) => a.distanceEnKm - b.distanceEnKm)
        .map((voeu) => formation?.voeux.find(({ id }) => voeu.id === id))
        .filter((voeu) => voeu !== undefined),
    [rayonSélectionné, voeuxÀProximitéDeLaCommune],
  );

  const changerRayonSélectionné = (rayon: (typeof rayons)[number]) => {
    setRayonSélectionné(rayon);
  };

  return {
    voeux: voeuxTriés,
    rayonSélectionné,
    changerRayonSélectionné,
    rayons,
  };
}
