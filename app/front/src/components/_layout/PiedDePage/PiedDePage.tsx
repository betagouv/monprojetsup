import usePiedDePage from "./usePiedDePage";
import useLayout from "@/components/_layout/useLayout";
import { constantes } from "@/configuration/constantes.ts";
import { Footer } from "@codegouvfr/react-dsfr/Footer";

const PiedDePage = () => {
    const { logoFooter, blocMarque, lienAccueil } = useLayout();
    const { liensSupplémentaires } = usePiedDePage();



    return (
        <Footer
            accessibility="partially compliant"
            accessibilityLinkProps={{ to: "/declaration-accessiblite" }}
            bottomItems={liensSupplémentaires}
            brandTop={blocMarque}
            homeLinkProps={lienAccueil}
            operatorLogo={logoFooter}
            termsLinkProps={{ href: constantes.LIENS.PAGE_MENTIONS_LÉGALES }}
            websiteMapLinkProps={{ to: "/plan-du-site" }}
            license={null}
            domains={[
                'education.gouv.fr',
                'enseignementsup-recherche.gouv.fr',
                'onisep.fr'
            ]}
        />
    );
};

export default PiedDePage;
