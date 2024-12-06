import { type ExplicationCorrespondanceÉlementFicheFormationProps } from "./ExplicationCorrespondanceÉlementFicheFormation.interface";

const ExplicationCorrespondanceÉlementFicheFormation = ({
  texteIntroductif,
  texteMisEnAvant,
}: ExplicationCorrespondanceÉlementFicheFormationProps) => {
  return (
    <li className="grid grid-flow-col justify-start gap-2 p-0">
      <span
        aria-hidden="true"
        className="fr-icon-checkbox-fill fr-icon--sm text-[--background-flat-success]"
      />
      <span>
        {texteIntroductif} <strong>{texteMisEnAvant}</strong>
      </span>
    </li>
  );
};

export default ExplicationCorrespondanceÉlementFicheFormation;
