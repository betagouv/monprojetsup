import { type TagProps } from "./Tag.interface";

const Tag = ({ libellé, taille }: TagProps) => {
  const classEnFonctionDeLaTaille = () => {
    if (taille === "petit") return "fr-tag--sm";
    return "";
  };

  return <p className={`fr-tag ${classEnFonctionDeLaTaille()}`}>{libellé}</p>;
};

export default Tag;
