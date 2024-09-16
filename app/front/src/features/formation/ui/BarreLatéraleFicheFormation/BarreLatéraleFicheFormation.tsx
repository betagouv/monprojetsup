import { type BarreLatéraleFicheFormationProps } from "./BarreLatéraleFicheFormation.interface";
import AnimationChargement from "@/components/AnimationChargement/AnimationChargement";
import BoutonRetourAuxSuggestions from "@/features/formation/ui/BoutonRetourAuxSuggestions/BoutonRetourAuxSuggestions";
import ListeFormations from "@/features/formation/ui/ListeFormations/ListeFormations";
import RechercheFormations from "@/features/formation/ui/RechercheFormations/RechercheFormations";

const BarreLatéraleFicheFormation = ({
  recherche,
  suggestions,
  résultatsDeRecherche,
}: BarreLatéraleFicheFormationProps) => {
  return (
    <>
      <div className="grid gap-6 px-2 lg:px-7">
        <div className="[&_.fr-input]:bg-white">
          <RechercheFormations valeurParDéfaut={recherche} />
        </div>
        {résultatsDeRecherche?.[0]?.id && <BoutonRetourAuxSuggestions />}
      </div>
      {!suggestions && !résultatsDeRecherche ? (
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
