import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";
import { actionsFicheFormationStore } from "@/features/formation/ui/store/useFicheFormation/useFicheFormation";
import { useRouter } from "@tanstack/react-router";

const BoutonRetourAuxSuggestions = () => {
  const router = useRouter();
  const { changerFormationAffichéeId } = actionsFicheFormationStore();

  const retournerAuxSuggestions = async () => {
    changerFormationAffichéeId(undefined);
    await router.navigate({ to: "/formations" });
  };

  return (
    <div className="text-center hover:[&_button]:bg-inherit">
      <Bouton
        auClic={() => retournerAuxSuggestions()}
        icône={{ position: "gauche", classe: "fr-icon-arrow-go-back-fill" }}
        label={i18n.PAGE_FORMATION.RETOUR_AUX_SUGGESTIONS}
        type="button"
        variante="secondaire"
      />
    </div>
  );
};

export default BoutonRetourAuxSuggestions;
