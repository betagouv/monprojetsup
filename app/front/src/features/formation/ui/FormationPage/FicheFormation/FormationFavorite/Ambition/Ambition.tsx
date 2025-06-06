import useAmbition from "./useAmbition.tsx";
import TagFiltreAvecEmoji from "@/components/TagFiltreAvecEmoji/TagFiltreAvecEmoji";
import Titre from "@/components/Titre/Titre.tsx";
import { i18n } from "@/configuration/i18n/i18n";

const Ambition = () => {
  const { ambitions, mettreAJourAmbition, key, ambitionActuelle } = useAmbition();

  return (
    <div>
      <div className="*:mb-4">
        <Titre
          niveauDeTitre="h3"
          styleDeTitre="text--lg"
        >
          {i18n.PAGE_FORMATION.CHOIX.AMBITIONS.TITRE}
        </Titre>
      </div>
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

export default Ambition;
