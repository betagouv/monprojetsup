import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useUtilisateur from "@/features/utilisateur/ui/hooks/useUtilisateur/useUtilisateur";
import { HeaderProps } from "@codegouvfr/react-dsfr/Header";
import { useRouterState } from "@tanstack/react-router";
import { useMemo } from "react";

export default function useEntête() {
  const router = useRouterState();
  const utilisateur = useUtilisateur();

  const navigation = useMemo((): HeaderProps["navigation"] => {
    if (router.location.pathname.includes(constantes.ÉLÈVE.PATH_PARCOURS_INSCRIPTION) || !utilisateur.id) {
      return null;
    }

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
      },
    ];
  }, [router.location.pathname, utilisateur.id]);

  const accèsRapides = useMemo((): HeaderProps["quickAccessItems"] => {
    if (router.location.pathname.includes(constantes.ÉLÈVE.PATH_PARCOURS_INSCRIPTION)) {
      return [
        {
          iconId: "fr-icon-close-line",
          buttonProps: {
            onClick: () => utilisateur.seDéconnecter,
          },
          text: i18n.PAGE_PROFIL.SE_DÉCONNECTER,
        },
      ];
    }

    if (!utilisateur.id) {
      return [
        {
          iconId: "fr-icon-user-fill",
          linkProps: {
            to: "/",
          },
          text: i18n.ENTÊTE.SE_CONNECTER,
        },
      ];
    }

    return [
      {
        iconId: "fr-icon-arrow-go-back-fill",
        linkProps: {
          href: constantes.LIENS.AVENIRS,
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
  }, [router.location.pathname, utilisateur]);

  return {
    navigation,
    accèsRapides,
  };
}
