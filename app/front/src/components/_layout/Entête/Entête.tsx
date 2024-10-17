import useEntête from "./useEntête";
import useLayout from "@/components/_layout/useLayout";
import { i18n } from "@/configuration/i18n/i18n";
import { Header } from "@codegouvfr/react-dsfr/Header";

const Entête = () => {
  const { navigation, accèsRapides } = useEntête();
  const { logoOpérateur, blocMarque, lienAccueil } = useLayout();

  return (
    <Header
      brandTop={blocMarque}
      homeLinkProps={lienAccueil}
      navigation={navigation}
      operatorLogo={logoOpérateur}
      quickAccessItems={accèsRapides}
      serviceTagline={i18n.ENTÊTE.DESCRIPTION_SERVICE}
      serviceTitle={i18n.APP.NOM}
    />
  );
};

export default Entête;
