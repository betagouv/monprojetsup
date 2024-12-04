import usePiedDePage from "./usePiedDePage";
import useLayout from "@/components/_layout/useLayout";
import { constantes } from "@/configuration/constantes.ts";
import { i18n } from "@/configuration/i18n/i18n";
import { Footer } from "@codegouvfr/react-dsfr/Footer";

const PiedDePage = () => {
  const { logoOpérateur, blocMarque, lienAccueil } = useLayout();
  const { partenaires, liensSupplémentaires } = usePiedDePage();

  return (
    <Footer
      accessibility="partially compliant"
      accessibilityLinkProps={{ to: "/" }}
      bottomItems={liensSupplémentaires}
      brandTop={blocMarque}
      contentDescription={i18n.PIED_DE_PAGE.DESCRIPTION_SERVICE}
      homeLinkProps={lienAccueil}
      operatorLogo={logoOpérateur}
      partnersLogos={partenaires}
      termsLinkProps={{ href: constantes.LIENS.PAGE_MENTIONS_LÉGALES }}
      websiteMapLinkProps={{ to: "/plan-du-site" }}
    />
  );
};

export default PiedDePage;
