import useMaSélectionVoeux from "./useMaSélectionVoeux";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import ListeDeVoeux from "@/features/formation/ui/FicheFormation/ChoixFormation/Voeux/ListeDeVoeux/ListeDeVoeux";

const MaSélectionVoeux = () => {
  const { voeuxSélectionnés } = useMaSélectionVoeux();

  return (
    <>
      <div className="*:mb-2">
        <Titre
          niveauDeTitre="h4"
          styleDeTitre="text--md"
        >
          {i18n.PAGE_FORMATION.CHOIX.VOEUX.MA_SÉLECTION.TITRE}
        </Titre>
      </div>
      {voeuxSélectionnés.length > 0 ? (
        <ListeDeVoeux voeux={voeuxSélectionnés} />
      ) : (
        <p className="fr-text--sm mb-0"> {i18n.PAGE_FORMATION.CHOIX.VOEUX.MA_SÉLECTION.AUCUN}</p>
      )}
    </>
  );
};

export default MaSélectionVoeux;
