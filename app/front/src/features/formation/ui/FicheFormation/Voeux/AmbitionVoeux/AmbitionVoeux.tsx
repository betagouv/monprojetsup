import { type AmbitionVoeuxProps } from "./AmbitionVoeux.interface";
import useAmbitionVoeux from "./useAmbitionVoeux";
import TagFiltreAvecEmoji from "@/components/TagFiltreAvecEmoji/TagFiltreAvecEmoji";
import { i18n } from "@/configuration/i18n/i18n";

const AmbitionVoeux = ({ ambitionActuelle, formationId }: AmbitionVoeuxProps) => {
  const { ambitions, mettreAJourAmbition, key } = useAmbitionVoeux({ formationId });

  return (
    <div>
      <p className="mb-4 font-medium text-[--text-label-grey]">{i18n.PAGE_FORMATION.VOEUX.AMBITIONS.LÉGENDE}</p>
      <ul className="m-0 flex list-none flex-wrap justify-start gap-4 p-0">
        {ambitions.map((ambition) => (
          <li key={ambition.niveau}>
            <TagFiltreAvecEmoji
              appuyéParDéfaut={ambitionActuelle === ambition.niveau}
              auClic={() => mettreAJourAmbition(ambition.niveau)}
              emoji={ambition.emoji}
              key={key}
            >
              {ambition.libellé}
            </TagFiltreAvecEmoji>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AmbitionVoeux;
