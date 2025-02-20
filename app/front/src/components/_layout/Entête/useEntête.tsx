import { environnement } from "@/configuration/environnement.ts";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useUtilisateur from "@/features/utilisateur/ui/useUtilisateur";
import { HeaderProps } from "@codegouvfr/react-dsfr/Header";
import { useMemo } from "react";

export default function useEntête() {
  const utilisateur = useUtilisateur();
  const élève = useÉlève();

  const afficherFavoris = élève.élèveAuMoinsUneFormationFavorite || élève.élèveAuMoinsUnMétierFavori;
  const aUnProfilPermettantUneExpériencePersonnalisée = élève.aUnProfilPermettantUneExpériencePersonnalisée;

  const navigation = useMemo((): HeaderProps["navigation"] => {
    return [
      {
        text: i18n.NAVIGATION.TABLEAU_DE_BORD,
        linkProps: { to: "/" },
      },
      {
        text: i18n.NAVIGATION.FORMATIONS,
        linkProps: { to: "/formations" },
      },
      {
        text: i18n.NAVIGATION.FAVORIS,
        linkProps: { to: "/favoris" },
        className: afficherFavoris ? "" : "hidden",
      },
      {
        text: i18n.NAVIGATION.PROFIL,
        linkProps: { to: "/profil" },
        className: aUnProfilPermettantUneExpériencePersonnalisée ? "" : "hidden",
      },
      {
        text: i18n.NAVIGATION.PROFIL,
        linkProps: { to: "/eleve/inscription/projet" },
        className: aUnProfilPermettantUneExpériencePersonnalisée ? "hidden" : "",
      },
    ];
  }, [aUnProfilPermettantUneExpériencePersonnalisée, afficherFavoris]);

  const accèsRapides = useMemo((): HeaderProps["quickAccessItems"] => {
    if (utilisateur.estAuthentifié) {
      return [
        {
          iconId: "fr-icon-close-line",
          buttonProps: {
            onClick: () => {
              void (async () => await utilisateur.seDéconnecter())();
            },
          },
          text: i18n.PAGE_PROFIL.SE_DÉCONNECTER,
        },
        {
          iconId: "fr-icon-arrow-go-back-fill",
          linkProps: {
            href: environnement.VITE_AVENIRS_URL,
            className: "after:!content-none",
          },
          text: i18n.ENTÊTE.PLATEFORME_AVENIRS,
        },
        {
          iconId: "fr-icon-user-fill",
          linkProps: {
            to: "/profil",
          },
          text: `${utilisateur.prénom} ${utilisateur.nom}`,
        },
      ];
    } else {
      return [
        {
          iconId: "fr-icon-arrow-go-back-fill",
          linkProps: {
            href: environnement.VITE_AVENIRS_URL,
            className: "after:!content-none",
          },
          text: i18n.ENTÊTE.PLATEFORME_AVENIRS,
        },
        {
          iconId: "fr-icon-user-fill",
          linkProps: {
            to: "/connexion",
          },
          text: i18n.ENTÊTE.SE_CONNECTER,
        },
      ];
    }
  }, [utilisateur]);

  return {
    navigation,
    accèsRapides,
  };
}
