import useModaleConnexionParcourSup from "./useModaleConnexionParcourSup";
import Bouton from "@/components/Bouton/Bouton";

const ModaleConnexionParcourSup = () => {
  const { redirigerVersAuthParcourSup } = useModaleConnexionParcourSup();

  return (
    <Bouton
      auClic={redirigerVersAuthParcourSup}
      label="Associer à PS"
      type="button"
    />
  );
};

export default ModaleConnexionParcourSup;
