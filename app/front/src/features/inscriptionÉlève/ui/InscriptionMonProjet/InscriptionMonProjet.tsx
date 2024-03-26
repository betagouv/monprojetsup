import IndicateurÉtapes from "@/components/_dsfr/IndicateurÉtapes/IndicateurÉtapes";

const InscriptionMonProjet = () => {
  return (
    <IndicateurÉtapes
      étapeActuelle={2}
      étapes={["step 1", "step 2"]}
    />
  );
};

export default InscriptionMonProjet;
