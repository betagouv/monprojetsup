import logoOnisep from "@/assets/logo-onisep.svg";
import logoMPS from "@/assets/logo/logo-mps.svg";
import { HeaderProps } from "@codegouvfr/react-dsfr/Header";

export default function useLayout() {
  const logoOpérateur: HeaderProps["operatorLogo"] = {
    alt: "MPS",
    imgUrl: logoMPS,
    orientation: "horizontal",
  };

  const logoFooter: HeaderProps["operatorLogo"] = {
    alt: "ONISEP",
    imgUrl: logoOnisep,
    orientation: "horizontal",
  };

  const blocMarque: HeaderProps["brandTop"] = (
    <>
      République
      <br role="presentation" />
      Française
    </>
  );

  const lienAccueil: HeaderProps["homeLinkProps"] = {
    to: "https://www.onisep.fr",
    title: `ONISEP`,
  };

  return {
    logoOpérateur,
    logoFooter,
    blocMarque,
    lienAccueil,
  };
}
