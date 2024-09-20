import explorerSVG from "@/assets/explorer.svg";
import favorisSVG from "@/assets/favoris.svg";
import profilSVG from "@/assets/profil.svg";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { type CarteTableauDeBordÉlèveProps } from "@/features/élève/ui/tableauDeBord/CarteTableauDeBordÉlève/CarteTableauDeBordÉlève.interface";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useTableauDeBordÉlève() {
  useSuspenseQuery(élèveQueryOptions);
  const { data: élève } = useQuery(élèveQueryOptions);

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

  const témoignage = useMemo(() => {
    let contenu: string;

    if (élève?.classe === "terminale") {
      contenu = i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.TERMINALE;
    } else if (élève?.classe === "premiere") {
      contenu = i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.PREMIÈRE;
    } else {
      contenu = i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.SECONDE;
    }

    return {
      contenu,
      auteur: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.AUTEUR,
      rôle: i18n.ÉLÈVE.TABLEAU_DE_BORD.TÉMOIGNAGE.RÔLE,
    };
  }, [élève]);

  return {
    témoignage,
    cartes,
  };
}
