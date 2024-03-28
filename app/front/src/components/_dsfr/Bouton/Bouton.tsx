/* eslint-disable react/button-has-type */
import { type BoutonProps } from "./Bouton.interface";

const Bouton = ({ label, taille, variante, desactivé, icône, type, auClic }: BoutonProps) => {
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
    <button
      className={`fr-btn ${classEnFonctionDeLaTaille()} ${classEnFonctionDeLaVariante()} ${classEnFonctionDeLIcône()}`}
      disabled={desactivé}
      onClick={auClic}
      type={type}
    >
      {label}
    </button>
  );
};

export default Bouton;
