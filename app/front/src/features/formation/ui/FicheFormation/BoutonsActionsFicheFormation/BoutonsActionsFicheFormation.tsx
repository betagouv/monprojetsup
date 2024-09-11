import { type BoutonsActionsFicheFormationProps } from "./BoutonsActionsFicheFormation.interface";
import useBoutonsActionsFicheFormation from "./useBoutonsActionsFicheFormation";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const BoutonsActionsFicheFormation = ({ formation }: BoutonsActionsFicheFormationProps) => {
  const TAILLE_BOUTON = "grand";
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
    <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
      {!estMasquée && !estFavorite && (
        <Bouton
          auClic={ajouterEnFavori}
          icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
          label={i18n.COMMUN.AJOUTER_À_MA_SÉLECTION}
          taille={TAILLE_BOUTON}
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
            {i18n.COMMUN.AJOUTÉE_À_MA_SÉLECTION}
          </div>
          <Bouton
            auClic={supprimerDesFavoris}
            icône={{ position: "gauche", classe: "fr-icon-close-line" }}
            label={i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION}
            taille={TAILLE_BOUTON}
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
          taille={TAILLE_BOUTON}
          type="button"
          variante="secondaire"
        />
      )}
      {!estFavorite && estMasquée && (
        <Bouton
          auClic={afficherÀNouveauUneFormation}
          icône={{ position: "gauche", classe: "fr-icon-eye-line" }}
          label={i18n.COMMUN.AFFICHER_À_NOUVEAU}
          taille={TAILLE_BOUTON}
          type="button"
        />
      )}
    </div>
  );
};

export default BoutonsActionsFicheFormation;
