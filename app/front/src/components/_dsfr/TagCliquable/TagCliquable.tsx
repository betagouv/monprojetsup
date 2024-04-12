import { type TagCliquableProps } from "./TagCliquable.interface";
import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";

const TagCliquable = ({ libellé, taille, hrefInterne, hrefExterne }: TagCliquableProps) => {
  if (hrefExterne) {
    return (
      <LienExterne
        ariaLabel={libellé}
        estUnTag
        href={hrefExterne}
        taille={taille === "sm" ? "petit" : undefined}
        variante="neutre"
      >
        {libellé}
      </LienExterne>
    );
  }

  return (
    <LienInterne
      ariaLabel={libellé}
      estUnTag
      href={hrefInterne}
      taille={taille === "sm" ? "petit" : undefined}
      variante="neutre"
    >
      {libellé}
    </LienInterne>
  );
};

export default TagCliquable;
