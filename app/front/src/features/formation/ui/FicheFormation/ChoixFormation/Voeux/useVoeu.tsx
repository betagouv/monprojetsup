import { Favori } from "@/components/SélecteurFavoris/Favori/Favori.interface";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { Voeu } from "@/features/formation/domain/formation.interface";
import { Bac } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { useQuery } from "@tanstack/react-query";

export default function useVoeu() {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { élève, mettreÀJourÉlève } = useÉlève({});

  const générerUrlParcoursup = (voeuId: Voeu["id"], bacs: Bac[]) => {
    const idTypeBacParcoursup = bacs.find((baccalaureat) => baccalaureat.id === élève?.bac)?.idCarteParcoursup;

    const idVoeuParcoursup = voeuId.replaceAll(/\D/gu, "");

    const url = new URL(constantes.LIENS.FICHE_VOEU_PARCOURSUP);
    url.searchParams.set("g_ta_cod", idVoeuParcoursup);
    url.searchParams.set("originePc", "0");

    if (idTypeBacParcoursup) url.searchParams.set("typeBac", idTypeBacParcoursup);

    return `${url.toString()}`;
  };

  const mettreÀJour = async (voeuId: Voeu["id"], estFavori: boolean) => {
    if (!élève) return;

    if (estFavori) {
      await mettreÀJourÉlève({
        voeuxFavoris: élève.voeuxFavoris?.filter((voeuFavori) => voeuFavori.id !== voeuId),
      });
    } else {
      await mettreÀJourÉlève({
        voeuxFavoris: [...(élève.voeuxFavoris ?? []), { id: voeuId, estParcoursup: false }],
      });
    }
  };

  const voeuVersFavori = (voeu: Voeu): Favori => {
    const estFavori = élève?.voeuxFavoris?.some((voeuFavori) => voeuFavori.id === voeu.id) ?? false;
    const estFavoriParcoursup =
      élève?.voeuxFavoris?.find((voeuFavori) => voeuFavori.id === voeu.id)?.estParcoursup ?? false;

    return {
      id: voeu.id,
      nom: voeu.nom,
      estFavori,
      ariaLabel: estFavoriParcoursup ? i18n.ACCESSIBILITÉ.FAVORI_PARCOURSUP : undefined,
      title: estFavoriParcoursup ? i18n.ACCESSIBILITÉ.FAVORI_PARCOURSUP : undefined,
      url: générerUrlParcoursup(voeu.id, référentielDonnées?.bacs ?? []),
      désactivé: estFavoriParcoursup,
      icôneEstFavori: estFavoriParcoursup ? "fr-icon-custom-parcoursup" : undefined,
      icôneEstPasFavori: estFavoriParcoursup ? "fr-icon-custom-parcoursup" : undefined,
      callbackMettreÀJour: (idFavori: string) => mettreÀJour(idFavori, estFavori),
    };
  };

  return {
    voeuVersFavori,
  };
}
