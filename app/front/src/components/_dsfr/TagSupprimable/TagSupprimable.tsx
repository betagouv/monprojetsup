import { type TagSupprimableProps } from "./TagSupprimable.interface";
import { i18n } from "@/configuration/i18n/i18n";

const TagSupprimable = ({ libellé, taille, auClicPourSupprimerLeTag }: TagSupprimableProps) => {
  const classEnFonctionDeLaTaille = () => {
    if (taille === "sm") return "fr-tag--sm";
    return "";
  };

  return (
    <button
      aria-label={`${i18n.ACCESSIBILITÉ.RETIRER} ${libellé}`}
      className={`fr-tag fr-tag--dismiss ${classEnFonctionDeLaTaille()}`}
      onClick={auClicPourSupprimerLeTag}
      type="button"
    >
      {libellé}
    </button>
  );
};

export default TagSupprimable;
