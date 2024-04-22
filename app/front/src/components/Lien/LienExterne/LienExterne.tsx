import { type LienExterneProps } from "./LienExterne.interface";
import useLien from "@/components/Lien/useLien";

const LienExterne = ({
  children,
  ariaLabel,
  href,
  taille,
  variante,
  icône,
  estUnTéléchargement,
  estUnTag,
}: LienExterneProps) => {
  const { ariaLabelFormaté, classesCSS, target } = useLien({
    ariaLabel,
    href,
    taille,
    variante,
    icône,
    estUnTéléchargement,
    estUnTag,
  });

  return (
    <a
      aria-label={ariaLabelFormaté}
      className={classesCSS}
      download={estUnTéléchargement}
      href={href}
      target={target}
    >
      {children}
    </a>
  );
};

export default LienExterne;
