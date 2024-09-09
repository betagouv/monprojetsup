import { type BoutonsActionsFormationMétierProps } from "./BoutonsActionsFormationMétier.interface";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const BoutonsActionsFormationMétier = ({
  estFavori,
  estUneFormation,
  ajouterEnFavoriCallback,
  supprimerDesFavoris,
  masquerCallback,
  taille,
}: BoutonsActionsFormationMétierProps) => {
  return (
    <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
      {estFavori ? (
        <>
          <div className="grid grid-flow-col items-center gap-2 font-medium text-[--artwork-minor-red-marianne]">
            <span
              aria-hidden="true"
              className="fr-icon-heart-fill"
            />
            {i18n.COMMUN.AJOUTÉE_À_MA_SÉLECTION}
          </div>
          <Bouton
            auClic={supprimerDesFavoris}
            icône={{ position: "gauche", classe: "fr-icon-close-line" }}
            label={i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION}
            taille={taille}
            type="button"
            variante="secondaire"
          />
        </>
      ) : (
        <Bouton
          auClic={ajouterEnFavoriCallback}
          icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
          label={i18n.COMMUN.AJOUTER_À_MA_SÉLECTION}
          taille={taille}
          type="button"
        />
      )}
      {!estFavori && estUneFormation && (
        <Bouton
          auClic={masquerCallback}
          icône={{ position: "gauche", classe: "fr-icon-eye-off-line" }}
          label={i18n.COMMUN.NE_PLUS_VOIR}
          taille={taille}
          type="button"
          variante="secondaire"
        />
      )}
    </div>
  );
};

export default BoutonsActionsFormationMétier;
