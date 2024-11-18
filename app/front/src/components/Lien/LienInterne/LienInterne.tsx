import { type LienInterneProps } from "./LienInterne.interface";
import useLien from "@/components/Lien/useLien";
import { Paths } from "@/types/commons";
import { Link } from "@tanstack/react-router";

const LienInterne = <H extends Paths>({
  children,
  ariaLabel,
  href,
  taille,
  variante,
  icône,
  estUnTag,
  hash,
  paramètresPath,
  paramètresSearch,
  auClic,
}: LienInterneProps<H>) => {
  const { ariaLabelFormaté, classesCSS, target } = useLien({ ariaLabel, href, taille, variante, icône, estUnTag });

  return (
    <Link
      aria-label={ariaLabelFormaté}
      className={classesCSS}
      hash={hash}
      onClick={auClic}
      params={paramètresPath}
      search={paramètresSearch}
      target={target}
      to={href}
    >
      {children}
    </Link>
  );
};

export default LienInterne;
