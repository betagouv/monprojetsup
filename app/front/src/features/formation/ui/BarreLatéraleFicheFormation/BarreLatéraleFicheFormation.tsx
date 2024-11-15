import { type BarreLatéraleFicheFormationProps } from "./BarreLatéraleFicheFormation.interface";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import BoutonRetourAuxSuggestions from "@/features/formation/ui/BoutonRetourAuxSuggestions/BoutonRetourAuxSuggestions";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import RechercheFormations from "@/features/formation/ui/RechercheFormations/RechercheFormations";

const BarreLatéraleFicheFormation = ({
  suggestions,
  résultatsDeRecherche,
  chargementEnCours,
}: BarreLatéraleFicheFormationProps) => {
  if (!suggestions && !résultatsDeRecherche) return null;

  return (
    <>
      <div className="grid gap-6 px-2 lg:px-7">
        <div className="[&_.fr-input]:bg-white">
          <RechercheFormations />
        </div>
        {résultatsDeRecherche?.[0]?.id && <BoutonRetourAuxSuggestions />}
      </div>

      {chargementEnCours ? (
        <AnimationChargement />
      ) : (
        <ListeFormations
          affichéSurLaPage="ficheFormation"
          formations={résultatsDeRecherche ?? suggestions ?? []}
        />
      )}
    </>
  );
};

export default BarreLatéraleFicheFormation;
