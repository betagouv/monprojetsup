import explorerSVG from "@/assets/explorer.svg";
import favorisSVG from "@/assets/favoris.svg";
import profilSVG from "@/assets/profil.svg";
import { actionsToastStore } from "@/components/Toast/useToastStore/useToastStore";
import { i18n } from "@/configuration/i18n/i18n";
import { CartePrimaireTableauDeBordÉlèveProps } from "@/features/élève/ui/TableauDeBordÉlèvePage/CartePrimaireTableauDeBordÉlève/CartePrimaireTableauDeBordÉlève.interface";
import { getRouteApi } from "@tanstack/react-router";

export default function useTableauDeBordÉlèvePage() {
  const route = getRouteApi("/_auth/");
  const { associationPS } = route.useSearch();
  const { déclencherToast } = actionsToastStore();

  if (associationPS === "ok") {
    déclencherToast(
      i18n.ÉLÈVE.TABLEAU_DE_BORD.TOAST_PARCOURSUP.SUCCÈS.TITRE,
      i18n.ÉLÈVE.TABLEAU_DE_BORD.TOAST_PARCOURSUP.SUCCÈS.DESCRIPTION,
      "success",
    );
  } else if (associationPS === "erreur") {
    déclencherToast(
      i18n.ÉLÈVE.TABLEAU_DE_BORD.TOAST_PARCOURSUP.ERREUR.TITRE,
      i18n.ÉLÈVE.TABLEAU_DE_BORD.TOAST_PARCOURSUP.ERREUR.DESCRIPTION,
      "error",
    );
  }

  const cartes: CartePrimaireTableauDeBordÉlèveProps[] = [
    {
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.SOUS_TITRE,
      illustration: explorerSVG,
      lien: "/formations",
    },
    {
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.SOUS_TITRE,
      illustration: favorisSVG,
      lien: "/favoris",
    },
    {
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.SOUS_TITRE,
      illustration: profilSVG,
      lien: "/profil",
    },
  ];

  return {
    cartes,
  };
}
