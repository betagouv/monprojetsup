import { type BoutonsActionsFicheFormationProps } from "./BoutonsActionsFicheFormation.interface";
import useBoutonsActionsFicheFormation from "./useBoutonsActionsFicheFormation";
import Bouton from "@/components/Bouton/Bouton";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import VoeuxFicheFormation from "@/features/formation/ui/FicheFormation/ChoixFormation/ChoixFormation.tsx";

const BoutonsActionsFicheFormation = ({ formation }: BoutonsActionsFicheFormationProps) => {
  const {
    estFavorite,
    estMasquée,
    ajouterEnFavori,
    supprimerDesFavoris,
    masquerUneFormation,
    afficherÀNouveauUneFormation,
  } = useBoutonsActionsFicheFormation({
    formation,
  });

  return (
    <div className="grid gap-6">
      <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
        {!estMasquée && !estFavorite && (
          <Bouton
            auClic={ajouterEnFavori}
            icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
            label={i18n.COMMUN.AJOUTER_À_MA_SÉLECTION}
            taille={constantes.FICHE_FORMATION.TAILLE_BOUTONS_ACTIONS}
            type="button"
          />
        )}
        {estFavorite && (
          <>
            <div className="grid grid-flow-col items-center gap-2 font-medium text-[--artwork-minor-red-marianne]">
              <span
                aria-hidden="true"
                className="fr-icon-heart-fill"
              />
              {i18n.COMMUN.AJOUTÉ_À_MA_SÉLECTION}
            </div>
            <Bouton
              auClic={supprimerDesFavoris}
              icône={{ position: "gauche", classe: "fr-icon-close-line" }}
              label={i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION}
              taille={constantes.FICHE_FORMATION.TAILLE_BOUTONS_ACTIONS}
              type="button"
              variante="secondaire"
            />
          </>
        )}
        {!estFavorite && !estMasquée && (
          <Bouton
            auClic={masquerUneFormation}
            icône={{ position: "gauche", classe: "fr-icon-eye-off-line" }}
            label={i18n.COMMUN.NE_PLUS_VOIR}
            taille={constantes.FICHE_FORMATION.TAILLE_BOUTONS_ACTIONS}
            type="button"
            variante="secondaire"
          />
        )}
        {!estFavorite && estMasquée && (
          <Bouton
            auClic={afficherÀNouveauUneFormation}
            icône={{ position: "gauche", classe: "fr-icon-eye-line" }}
            label={i18n.COMMUN.AFFICHER_À_NOUVEAU}
            taille={constantes.FICHE_FORMATION.TAILLE_BOUTONS_ACTIONS}
            type="button"
          />
        )}
      </div>
      {estFavorite && (
        <section className="fr-accordion">
          <h2 className="fr-accordion__title">
            <button
              aria-controls="accordeon-voeux"
              aria-expanded="false"
              className="fr-accordion__btn"
              type="button"
            >
              {i18n.PAGE_FORMATION.CHOIX.TITRE}
            </button>
          </h2>
          <div
            className="fr-collapse"
            id="accordeon-voeux"
          >
            <VoeuxFicheFormation />
          </div>
        </section>
      )}
    </div>
  );
};

export default BoutonsActionsFicheFormation;
