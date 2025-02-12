import { type LienExterneProps } from "./LienExterne.interface";
import useLien from "@/components/Lien/useLien";
import { dépendances } from "@/configuration/dépendances/dépendances";

const LienExterne = ({
  children,
  ariaLabel,
  href,
  taille,
  variante,
  icône,
  estUnTéléchargement,
  estUnTag,
  id,
}: LienExterneProps) => {
  const { ariaLabelFormaté, classesCSS } = useLien({
    ariaLabel,
    href,
    taille,
    variante,
    icône,
    estUnTéléchargement,
    estUnTag,
  });

  const auClicHandler = () => dépendances.suivreLienExterne.run(id ?? "", href);

  return (
    <a
      aria-label={ariaLabelFormaté}
      className={classesCSS}
      download={estUnTéléchargement}
      href={href}
      onClick={auClicHandler}
      rel="noreferrer noopener"
      target="_blank"
    >
      {children}
    </a>
  );
};

export default LienExterne;
