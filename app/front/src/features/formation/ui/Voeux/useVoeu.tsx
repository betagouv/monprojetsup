import parcoursupSVG from "@/assets/parcoursup-fav.svg";
import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { Voeu } from "@/features/formation/domain/formation.interface";
import { Bac } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";

export default function useVoeu() {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { élève, estVoeuFavoriPourÉlève, estVoeuFavoriProvenantDeParcoursupPourÉlève } = useÉlève();
  const { mettreÀJourVoeuxÉlève } = useÉlèveMutation();

  const générerUrlParcoursup = (idVoeu: Voeu["id"], bacs: Bac[]) => {
    const idTypeBacParcoursup = bacs.find((baccalaureat) => baccalaureat.id === élève?.bac)?.idCarteParcoursup;

    const idVoeuParcoursup = idVoeu.replaceAll(/\D/gu, "");

    const url = new URL(constantes.LIENS.FICHE_VOEU_PARCOURSUP);
    url.searchParams.set("g_ta_cod", idVoeuParcoursup);
    url.searchParams.set("originePc", "0");

    if (idTypeBacParcoursup) url.searchParams.set("typeBac", idTypeBacParcoursup);

    return `${url.toString()}`;
  };

  const voeuVersFavori = (voeu: Voeu): Favori => {
    const estFavoriParcoursup = estVoeuFavoriProvenantDeParcoursupPourÉlève(voeu.id);

    return {
      id: voeu.id,
      nom: voeu.nom,
      estFavori: estVoeuFavoriPourÉlève(voeu.id),
      ariaLabel: estFavoriParcoursup ? i18n.ACCESSIBILITÉ.FAVORI_PARCOURSUP : undefined,
      title: estFavoriParcoursup ? i18n.ACCESSIBILITÉ.FAVORI_PARCOURSUP : undefined,
      url: générerUrlParcoursup(voeu.id, référentielDonnées?.bacs ?? []),
      désactivé: estFavoriParcoursup,
      icôneEstFavori: estFavoriParcoursup ? parcoursupSVG : undefined,
      icôneEstPasFavori: estFavoriParcoursup ? parcoursupSVG : undefined,
      callbackMettreÀJour: () => mettreÀJourVoeuxÉlève([voeu.id]),
    };
  };

  return {
    voeuVersFavori,
  };
}
