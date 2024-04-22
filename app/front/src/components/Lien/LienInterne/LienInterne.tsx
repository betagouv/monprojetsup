import { type LienInterneProps } from "./LienInterne.interface";
import useLien from "@/components/Lien/useLien";
import { Link } from "@tanstack/react-router";

const LienInterne = ({ children, ariaLabel, href, taille, variante, icône, estUnTag }: LienInterneProps) => {
  const { ariaLabelFormaté, classesCSS, target } = useLien({ ariaLabel, href, taille, variante, icône, estUnTag });

  return (
    <Link
      aria-label={ariaLabelFormaté}
      className={classesCSS}
      target={target}
      to={href}
    >
      {children}
    </Link>
  );
};

export default LienInterne;
