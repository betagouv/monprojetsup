import { type BoutonsActionsModalMétierProps } from "./BoutonsActionsMétier.interface";
import useBoutonsActionsMétier from "./useBoutonsActionsMétier";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const BoutonsActionsModalMétier = ({ métier, taille, ariaControls }: BoutonsActionsModalMétierProps) => {
  const { estFavori, ajouterEnFavori, supprimerDesFavoris } = useBoutonsActionsMétier({
    métier,
  });

  return (
    <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
      <Bouton
        ariaControls={ariaControls}
        label={i18n.COMMUN.FERMER}
        taille={taille}
        type="button"
        variante="tertiaire"
      />
      {!estFavori && (
        <Bouton
          auClic={ajouterEnFavori}
          icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
          label={i18n.COMMUN.AJOUTER_À_MA_SÉLECTION}
          taille={taille}
          type="button"
        />
      )}
      {estFavori && (
        <>
          <Bouton
            auClic={supprimerDesFavoris}
            icône={{ position: "gauche", classe: "fr-icon-close-line" }}
            label={i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION}
            taille={taille}
            type="button"
            variante="secondaire"
          />
          <div className="grid grid-flow-col items-center gap-2 font-medium text-[--artwork-minor-red-marianne]">
            <span
              aria-hidden="true"
              className="fr-icon-heart-fill"
            />
            {i18n.COMMUN.SÉLECTIONNÉ}
          </div>
        </>
      )}
    </div>
  );
};

export default BoutonsActionsModalMétier;
