import { type TagCliquableProps } from "./TagCliquable.interface";
import classes from "./TagCliquable.module.scss";
import { i18n } from "@/configuration/i18n/i18n";

const TagCliquable = ({ libellé, auClic, taille, supprimable }: TagCliquableProps) => {
  const classEnFonctionDeLaTaille = () => {
    if (taille === "petit") return "fr-tag--sm";

    return "";
  };

  const classEnFonctionDeSupprimable = () => {
    if (supprimable) return classes.tagDismiss;

    return "";
  };

  return (
    <button
      aria-label={`${supprimable ? i18n.ACCESSIBILITÉ.RETIRER : ""} ${libellé}`}
      className={`fr-tag ${classEnFonctionDeSupprimable()} ${classEnFonctionDeLaTaille()}`}
      onClick={auClic}
      type="button"
    >
      {libellé}
    </button>
  );
};

export default TagCliquable;
