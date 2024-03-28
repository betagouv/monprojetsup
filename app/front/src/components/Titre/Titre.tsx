/* eslint-disable react/forbid-component-props */
import { type TitreProps } from "./Titre.interface";

const Titre = ({ children, niveauDeTitre, styleDeTitre }: TitreProps) => {
  const Balise = niveauDeTitre as keyof JSX.IntrinsicElements;

  return <Balise className={styleDeTitre ? `fr-${styleDeTitre}` : ""}>{children}</Balise>;
};

export default Titre;
