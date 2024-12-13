import { type BoutonsActionsFicheFormationProps } from "./BoutonsActionsFicheFormation.interface";
import useBoutonsActionsFicheFormation from "./useBoutonsActionsFicheFormation";
import Bouton from "@/components/Bouton/Bouton";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import FormationFavorite from "@/features/formation/ui/FormationPage/FicheFormation/FormationFavorite/FormationFavorite";

const BoutonsActionsFicheFormation = ({ formation }: BoutonsActionsFicheFormationProps) => {
  const { estFavorite, estMasquée, mettreÀJourFormationsÉlève, mettreÀJourFormationsMasquéesÉlève } =
    useBoutonsActionsFicheFormation({
      formation,
    });

  return (
    <div className="grid gap-6">
      <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
        {!estMasquée && !estFavorite && (
          <Bouton
            auClic={() => mettreÀJourFormationsÉlève([formation.id])}
            icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
            taille={constantes.FORMATIONS.FICHES.TAILLE_BOUTONS_ACTIONS}
            type="button"
          >
            {i18n.COMMUN.AJOUTER_À_MA_SÉLECTION}
          </Bouton>
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
              auClic={() => mettreÀJourFormationsÉlève([formation.id])}
              icône={{ position: "gauche", classe: "fr-icon-close-line" }}
              taille={constantes.FORMATIONS.FICHES.TAILLE_BOUTONS_ACTIONS}
              type="button"
              variante="secondaire"
            >
              {i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION}
            </Bouton>
          </>
        )}
        {!estFavorite && !estMasquée && (
          <Bouton
            auClic={() => mettreÀJourFormationsMasquéesÉlève([formation.id])}
            icône={{ position: "gauche", classe: "fr-icon-eye-off-line" }}
            taille={constantes.FORMATIONS.FICHES.TAILLE_BOUTONS_ACTIONS}
            type="button"
            variante="secondaire"
          >
            {i18n.COMMUN.NE_PLUS_VOIR}
          </Bouton>
        )}
        {!estFavorite && estMasquée && (
          <Bouton
            auClic={() => mettreÀJourFormationsMasquéesÉlève([formation.id])}
            icône={{ position: "gauche", classe: "fr-icon-eye-line" }}
            taille={constantes.FORMATIONS.FICHES.TAILLE_BOUTONS_ACTIONS}
            type="button"
          >
            {i18n.COMMUN.AFFICHER_À_NOUVEAU}
          </Bouton>
        )}
      </div>
      {estFavorite && (
        <section className="fr-accordion">
          <h2 className="fr-accordion__title">
            <button
              aria-controls="accordeon-formation-favorite"
              aria-expanded="false"
              className="fr-accordion__btn"
              type="button"
            >
              {i18n.PAGE_FORMATION.CHOIX.TITRE}
            </button>
          </h2>
          <div
            className="fr-collapse"
            id="accordeon-formation-favorite"
          >
            <FormationFavorite key={formation.id} />
          </div>
        </section>
      )}
    </div>
  );
};

export default BoutonsActionsFicheFormation;
