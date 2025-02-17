import avenirsSVG from "@/assets/avenirs.svg";
import explorerSVG from "@/assets/explorer.svg";
import favorisSVG from "@/assets/favoris.svg";
import profilSVG from "@/assets/profil.svg";
import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveProgression from "@/features/élève/ui/hooks/useÉlèveProgression/useÉlèveProgression";
import { useAuth } from "react-oidc-context";

export default function useTableauDeBordÉlèvePage() {
  const élève = useÉlève();
  const progression = useÉlèveProgression().récupérerProgression;
  const auth = useAuth();
  const estAuthentifié = auth.isAuthenticated;
  const aAuMoinsUnDomaineFavori = élève.élèveAuMoinsUnDomaineFavori;
  const afficherLesSuggestions = aAuMoinsUnDomaineFavori;

  // useMemo((): CartePrimaireTableauDeBordÉlèveProps[] => {
  const result = [];

  if (afficherLesSuggestions) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.SUGGESTIONS.SOUS_TITRE,
      illustration: explorerSVG,
      lien: "/formations",
    });
  }

  if (élève.élèveAuMoinsUneFormationFavorite || élève.élèveAuMoinsUnMétierFavori) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.FAVORIS.SOUS_TITRE,
      illustration: favorisSVG,
      lien: "/favoris",
    });
  }

  if (afficherLesSuggestions) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL.SOUS_TITRE,
      illustration: profilSVG,
      lien: "/profil",
    });
  } else {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL_VIDE.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.PROFIL_VIDE.SOUS_TITRE,
      illustration: profilSVG,
      lien: "/eleve/inscription/projet",
    });
  }

  if (!estAuthentifié) {
    result.push({
      titre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.CONNECTE_TOI.TITRE,
      sousTitre: i18n.ÉLÈVE.TABLEAU_DE_BORD.CARTES.CONNECTE_TOI.SOUS_TITRE,
      illustration: avenirsSVG,
      lien: "/connexion",
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
