import { type LienInterneProps } from "./LienInterne.interface";
import useLien from "@/components/Lien/useLien";
import { type router } from "@/configuration/lib/tanstack-router";
import { Link } from "@tanstack/react-router";

const LienInterne = <H extends keyof (typeof router)["routesByPath"]>({
  children,
  ariaLabel,
  href,
  taille,
  variante,
  icône,
  estUnTag,
  hash,
  paramètresPath,
}: LienInterneProps<H>) => {
  const { ariaLabelFormaté, classesCSS, target } = useLien({ ariaLabel, href, taille, variante, icône, estUnTag });

  return (
    <Link
      aria-label={ariaLabelFormaté}
      className={classesCSS}
      hash={hash}
      params={paramètresPath}
      target={target}
      to={href}
    >
      {children}
    </Link>
  );
};

export default LienInterne;
