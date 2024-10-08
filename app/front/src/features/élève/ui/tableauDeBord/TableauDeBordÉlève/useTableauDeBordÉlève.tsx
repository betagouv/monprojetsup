import explorerSVG from "@/assets/explorer.svg";
import favorisSVG from "@/assets/favoris.svg";
import profilSVG from "@/assets/profil.svg";
import { i18n } from "@/configuration/i18n/i18n";
import { type CarteTableauDeBordÉlèveProps } from "@/features/élève/ui/tableauDeBord/CarteTableauDeBordÉlève/CarteTableauDeBordÉlève.interface";

export default function useTableauDeBordÉlève() {
  const cartes: CarteTableauDeBordÉlèveProps[] = [
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

  const témoignage = {
    contenu: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.PHRASE,
    auteur: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.AUTEUR,
    rôle: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.RÔLE,
  };

  return {
    témoignage,
    cartes,
  };
}
