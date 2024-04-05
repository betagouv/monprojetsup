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

    return "";
  };

  const classEnFonctionDeLIcône = () => {
    if (icône?.position === "droite") return `fr-btn--icon-right ${icône.classe}`;
    if (icône?.position === "gauche") return `fr-btn--icon-left ${icône.classe}`;

    return "";
  };

  return (
    <div
      className={`fr-btn ${classEnFonctionDeLaTaille()} ${classEnFonctionDeLaVariante()} ${classEnFonctionDeLIcône()}`}
    >
      {label}
    </div>
  );
};

export default BoutonSquelette;
