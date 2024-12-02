import { UseVoeuArgs } from "./Voeu.interface";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { constantes } from "@/configuration/constantes";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useVoeu({ voeu }: UseVoeuArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { élève, mettreÀJourÉlève } = useÉlève({});
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const estFavori = useMemo(
    () => élève?.voeuxFavoris?.some((voeuFavori) => voeuFavori.id === voeu.id) ?? false,
    [élève?.voeuxFavoris, voeu],
  );

  const estFavoriParcoursup = useMemo(
    () => élève?.voeuxFavoris?.find((voeuFavori) => voeuFavori.id === voeu.id)?.estParcoursup ?? false,
    [élève?.voeuxFavoris, voeu],
  );

  const urlParcoursup = useMemo((): string => {
    const idTypeBacParcoursup = référentielDonnées?.bacs.find(
      (baccalaureat) => baccalaureat.id === élève?.bac,
    )?.idCarteParcoursup;

    const idVoeuParcoursup = voeu.id.replaceAll(/\D/gu, "");

    const url = new URL(constantes.LIENS.FICHE_VOEU_PARCOURSUP);
    url.searchParams.set("g_ta_cod", idVoeuParcoursup);
    url.searchParams.set("originePc", "0");

    if (idTypeBacParcoursup) url.searchParams.set("typeBac", idTypeBacParcoursup);

    return `${url.toString()}`;
  }, [référentielDonnées?.bacs, voeu.id, élève?.bac]);

  const mettreÀJour = async () => {
    if (!élève || !formationAffichée?.id) return;

    if (estFavori) {
      await mettreÀJourÉlève({
        voeuxFavoris: élève.voeuxFavoris?.filter((voeuFavori) => voeuFavori.id !== voeu.id),
      });
    } else {
      await mettreÀJourÉlève({
        voeuxFavoris: [...(élève.voeuxFavoris ?? []), { id: voeu.id, estParcoursup: false }],
      });
    }
  };

  const classIcône = () => {
    if (estFavoriParcoursup) return "fr-icon-custom-parcoursup";
    if (estFavori) return "fr-icon-heart-fill";

    return "fr-icon-heart-line";
  };

  return {
    mettreÀJour,
    estFavori,
    estFavoriParcoursup,
    urlParcoursup,
    icône: classIcône(),
  };
}
