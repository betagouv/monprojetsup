import { actionsListeEtAperçuStore } from "@/components/_layout/ListeEtAperçuLayout/useListeEtAperçuStore/useListeEtAperçuStore";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const BoutonRetourAuxSuggestions = () => {
  const { réinitialiserRecherche } = actionsListeEtAperçuStore();

  const retournerAuxSuggestions = () => {
    réinitialiserRecherche();
    document.querySelector("#liste-formations")?.scrollTo({ top: 0 });
  };

  return (
    <div className="text-center hover:[&_button]:bg-inherit">
      <Bouton
        auClic={() => retournerAuxSuggestions()}
        icône={{ position: "gauche", classe: "fr-icon-arrow-go-back-fill" }}
        type="button"
        variante="secondaire"
      >
        {i18n.PAGE_FORMATION.RETOUR_AUX_SUGGESTIONS}
      </Bouton>
    </div>
  );
};

export default BoutonRetourAuxSuggestions;
