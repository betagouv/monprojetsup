import { type TagFiltreProps } from "./TagFiltre.interface";
import { useState } from "react";

const TagFiltre = ({ libellé, emoji, auClic, taille, appuyéParDéfaut = false }: TagFiltreProps) => {
  const [estAppuyé, setEstAppuyé] = useState(appuyéParDéfaut);
  const classEnFonctionDeLaTaille = () => {
    if (taille === "petit") return "fr-tag--sm";

    return "";
  };

  return (
    <button
      aria-label={libellé}
      aria-pressed={estAppuyé}
      className={`fr-tag ${classEnFonctionDeLaTaille()}`}
      onClick={() => {
        auClic(!estAppuyé);
        setEstAppuyé(!estAppuyé);
      }}
      type="button"
    >
      {emoji && (
        <>
          <span
            aria-hidden
            className="pr-2"
          >
            {emoji}
          </span>{" "}
        </>
      )}
      <span className="capitalize">{libellé}</span>
    </button>
  );
};

export default TagFiltre;
