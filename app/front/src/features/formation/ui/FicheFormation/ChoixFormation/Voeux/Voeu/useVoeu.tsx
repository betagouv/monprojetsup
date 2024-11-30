import { UseVoeuArgs } from "./Voeu.interface";
import { élémentAffichéListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/store/useListeEtAperçu/useListeEtAperçu";
import { constantes } from "@/configuration/constantes";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";

export default function useVoeu({ voeu }: UseVoeuArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { ajouterUnVoeuFavori, supprimerUnVoeuFavori, élève } = useÉlève({});
  const formationAffichée = élémentAffichéListeEtAperçuStore();

  const idsVoeuxSélectionnés = new Set(élève?.voeuxFavoris?.map((voeuFavori) => voeuFavori.id));
  const idsVoeuxParcoursup = new Set(
    élève?.voeuxFavoris?.filter((voeuFavori) => voeuFavori.estParcoursup).map((voeuFavori) => voeuFavori.id),
  );

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

    return Boolean(idsVoeuxSélectionnés.has(voeu.id));
  };

  const estFavoriParcoursup = (): boolean => {
    if (!élève) return false;

    return Boolean(idsVoeuxParcoursup.has(voeu.id));
  };

  const mettreÀJour = async () => {
    if (!élève || !formationAffichée?.id) return;

    if (idsVoeuxSélectionnés.has(voeu.id)) {
      idsVoeuxSélectionnés.delete(voeu.id);
      await supprimerUnVoeuFavori(voeu.id);
    } else {
      idsVoeuxSélectionnés.add(voeu.id);
      await ajouterUnVoeuFavori(voeu.id);
    }
  };

  return {
    mettreÀJour,
    estFavori,
    estFavoriParcoursup,
    urlParcoursup,
  };
}
