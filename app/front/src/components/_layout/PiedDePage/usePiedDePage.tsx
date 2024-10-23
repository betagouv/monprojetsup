import logoAvenirs from "@/assets/logo/logo-avenirs.svg";
import logoBetaGouv from "@/assets/logo/logo-beta-gouv.svg";
import logoCNRS from "@/assets/logo/logo-cnrs.svg";
import { i18n } from "@/configuration/i18n/i18n";
import { FooterProps } from "@codegouvfr/react-dsfr/Footer";

export default function usePiedDePage() {
  const partenaires: FooterProps["partnersLogos"] = {
    sub: [
      {
        imgUrl: logoCNRS,
        alt: "CNRS",
      },
      {
        imgUrl: logoBetaGouv,
        alt: "Beta.gouv.fr",
      },
      {
        imgUrl: logoAvenirs,
        alt: "Onisep Avenir(s)",
      },
    ],
  };

  const liensSupplémentaires: FooterProps["bottomItems"] = [
    {
      linkProps: { to: "/" },
      text: i18n.PIED_DE_PAGE.LIENS_INTERNES.PLAN_DU_SITE,
    },
    {
      linkProps: { to: "/" },
      text: i18n.PIED_DE_PAGE.LIENS_INTERNES.DONNÉES_PERSONNELLES,
    },
    {
      linkProps: { to: "/" },
      text: i18n.PIED_DE_PAGE.LIENS_INTERNES.GESTION_COOKIES,
    },
  ];

  return {
    partenaires,
    liensSupplémentaires,
  };
}
