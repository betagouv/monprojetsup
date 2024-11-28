import { UseVoeuArgs } from "./Voeu.interface";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { constantes } from "@/configuration/constantes";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";

export default function useVoeu({ voeu }: UseVoeuArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { mettreÀJourUneFormationFavorite, élève } = useÉlève({});
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const idsVoeuxSélectionnés =
    élève?.formationsFavorites?.find((formationFavorite) => formationFavorite.id === formationAffichée.id)?.voeux ?? [];

  const urlParcoursup = (): string => {
    const idTypeBacParcoursup = référentielDonnées?.bacs.find(
      (baccalaureat) => baccalaureat.id === élève?.bac,
    )?.idCarteParcoursup;

    const idVoeuParcoursup = voeu.id.replaceAll(/\D/gu, "");

    const url = new URL(constantes.LIENS.FICHE_VOEU_PARCOURSUP);
    url.searchParams.set("g_ta_cod", idVoeuParcoursup);
    url.searchParams.set("originePc", "0");

    if (idTypeBacParcoursup) url.searchParams.set("typeBac", idTypeBacParcoursup);

    return `${url.toString()}`;
  };

  const estFavori = (): boolean => {
    if (!élève) return false;

    return Boolean(idsVoeuxSélectionnés?.includes(voeu.id));
  };

  const mettreÀJour = async () => {
    if (!élève || !formationAffichée?.id) return;

    const voeux = new Set(idsVoeuxSélectionnés);

    if (voeux.has(voeu.id)) {
      voeux.delete(voeu.id);
    } else {
      voeux.add(voeu.id);
    }

    await mettreÀJourUneFormationFavorite(formationAffichée.id, {
      voeux: [...voeux],
    });
  };

  return {
    mettreÀJour,
    estFavori,
    urlParcoursup,
  };
}
