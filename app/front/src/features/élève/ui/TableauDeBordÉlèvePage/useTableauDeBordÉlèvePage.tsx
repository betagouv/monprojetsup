import avenirsSVG from "@/assets/avenirs.svg";
import explorerSVG from "@/assets/explorer.svg";
import favorisSVG from "@/assets/favoris.svg";
import profilSVG from "@/assets/profil.svg";
import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveProgression from "@/features/élève/ui/hooks/useÉlèveProgression/useÉlèveProgression";
import { Paths } from "@/types/commons";
import { useAuth } from "react-oidc-context";
import { getRouteApi } from "@tanstack/react-router";
import { actionsToastStore } from "@/components/Toast/useToastStore/useToastStore"

export default function useTableauDeBordÉlèvePage() {
  const route = getRouteApi("/_main");
  const { associationPS } = route.useSearch();
  const { déclencherToast } = actionsToastStore();

  const élève = useÉlève();
  const progression = useÉlèveProgression().récupérerProgression;
  const auth = useAuth();
  const estAuthentifié = auth.isAuthenticated;
  const aAuMoinsUnDomaineFavori = élève.élèveAuMoinsUnDomaineFavori;
  const afficherLesSuggestions = aAuMoinsUnDomaineFavori;


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

  const result = [];

  if (afficherLesSuggestions) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.SOUS_TITRE,
      illustration: explorerSVG,
      lien: "/formations" as Paths,
    });
  }

  if (élève.élèveAuMoinsUneFormationFavorite || élève.élèveAuMoinsUnMétierFavori) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.SOUS_TITRE,
      illustration: favorisSVG,
      lien: "/favoris" as Paths,
    });
  }

  if (afficherLesSuggestions) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.SOUS_TITRE,
      illustration: profilSVG,
      lien: "/profil" as Paths,
    });
  } else {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL_VIDE.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL_VIDE.SOUS_TITRE,
      illustration: profilSVG,
      lien: "/eleve/inscription/projet" as Paths,
    });
  }

  if (!estAuthentifié) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.CONNECTE_TOI.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.CONNECTE_TOI.SOUS_TITRE,
      illustration: avenirsSVG,
      lien: "/connexion" as Paths,
    });
  }

  const cartes = result;

  return {
    cartes,
    associationParcoursupPossible:
      estAuthentifié && environnement.VITE_PARCOURSUP_OAUTH2_URL && environnement.VITE_PARCOURSUP_OAUTH2_CLIENT,
    progression,
    estAuthentifié,
    aAuMoinsUnDomaineFavori,
  };
}
