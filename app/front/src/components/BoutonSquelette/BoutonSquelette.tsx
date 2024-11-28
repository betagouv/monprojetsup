import { type BoutonSqueletteProps } from "./BoutonSquelette.interface";

const BoutonSquelette = ({ label, taille, variante, icône }: BoutonSqueletteProps) => {
  const classEnFonctionDeLaTaille = () => {
    if (taille === "grand") return "fr-btn--lg";
    if (taille === "petit") return "fr-btn--sm";

    return "";
  };

  const classEnFonctionDeLaVariante = () => {
    if (variante === "secondaire") return "fr-btn--secondary";
    if (variante === "tertiaire") return "fr-btn--tertiary";
    if (variante === "quaternaire") return "fr-btn--tertiary-no-outline";
    if (variante === "quinaire") return "fr-btn--tertiary-no-outline fr-text--xs underline p-0";

    return "";
  };

  const classEnFonctionDeLIcône = () => {
    if (icône?.position === "droite") return `fr-btn--icon-right ${icône.classe}`;
    if (icône?.position === "gauche") return `fr-btn--icon-left ${icône.classe}`;
    if (icône?.classe) return icône.classe;

    return "";
  };

  return (
    <div
      className={`fr-btn break-all ${classEnFonctionDeLaTaille()} ${classEnFonctionDeLaVariante()} ${classEnFonctionDeLIcône()}`}
    >
      {label}
    </div>
  );
};

export default BoutonSquelette;
