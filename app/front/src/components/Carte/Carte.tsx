import { type CarteProps } from "./Carte.interface";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";

const Carte = ({ titre, estFavori, estMasqué, children, auClic, sélectionnée }: CarteProps) => {
  const classEnFonctionDeLaSélection = () => {
    if (sélectionnée) return "border-[--border-active-blue-france]";
    return "border-transparent";
  };

  return (
    <button
      className={`grid w-full max-w-[500px] gap-4 border-2 border-solid bg-[--background-default-grey] p-6 text-left shadow-md ${classEnFonctionDeLaSélection()}`}
      onClick={auClic}
      type="button"
    >
      <div className="grid grid-flow-col items-baseline justify-between gap-1">
        <div className="*:mb-0">
          <Titre
            niveauDeTitre="h2"
            styleDeTitre="h4"
          >
            {titre}
          </Titre>
        </div>
        {estFavori && (
          <div>
            <span
              aria-hidden="true"
              className="fr-icon-heart-fill fr-icon--sm rounded bg-[--background-contrast-error] px-1 text-[--text-default-error]"
            />
            <span className="sr-only">{i18n.ACCESSIBILITÉ.FAVORIS}</span>
          </div>
        )}
        {estMasqué && (
          <div>
            <span
              aria-hidden="true"
              className="fr-icon-eye-off-line fr-icon--sm rounded bg-[--background-alt-beige-gris-galet] px-1 text-[--text-mention-grey]"
            />
            <span className="sr-only">{i18n.ACCESSIBILITÉ.MASQUÉ}</span>
          </div>
        )}
      </div>
      {children}
    </button>
  );
};

export default Carte;
