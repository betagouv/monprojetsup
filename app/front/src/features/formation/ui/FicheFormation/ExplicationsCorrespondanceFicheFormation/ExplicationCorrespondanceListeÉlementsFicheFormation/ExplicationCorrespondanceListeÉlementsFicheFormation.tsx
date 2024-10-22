import { type ExplicationCorrespondanceListeÉlementsFicheFormationProps } from "./ExplicationCorrespondanceListeÉlementsFicheFormation.interface";
import { Tag } from "@codegouvfr/react-dsfr/Tag";

const ExplicationCorrespondanceListeÉlementsFicheFormation = ({
  texteIntroductif,
  éléments,
}: ExplicationCorrespondanceListeÉlementsFicheFormationProps) => {
  return (
    <li className="grid grid-flow-col justify-start gap-2 p-0">
      <span
        aria-hidden="true"
        className="fr-icon-checkbox-fill fr-icon--sm text-[--background-flat-success]"
      />
      <span>
        {texteIntroductif}
        <ul className="mb-0 mt-2 flex list-none flex-wrap justify-start gap-3 p-0">
          {éléments.map((élément) => (
            <li key={élément}>
              <Tag small>{élément}</Tag>
            </li>
          ))}
        </ul>
      </span>
    </li>
  );
};

export default ExplicationCorrespondanceListeÉlementsFicheFormation;
