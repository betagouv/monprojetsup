import { type TitreProps } from "./Titre.interface";

const Titre = ({ children, niveauDeTitre, styleDeTitre, couleurDeTitre }: TitreProps) => {
  const Balise = niveauDeTitre as keyof JSX.IntrinsicElements;
  let className = styleDeTitre ? `fr-${styleDeTitre}` : "";
  className = couleurDeTitre ? className + ` text-[${couleurDeTitre}]` : className;
  return <Balise className={className}>{children}</Balise>;
};

export default Titre;
