import useModaleConnexionParcourSup from "./useModaleConnexionParcourSup";

const ModaleConnexionParcourSup = () => {
  const { connecterLesComptesPSetMPS } = useModaleConnexionParcourSup();

  return (
    <button
      onClick={() => connecterLesComptesPSetMPS}
      type="button"
    >
      Associer à PS
    </button>
  );
};

export default ModaleConnexionParcourSup;
