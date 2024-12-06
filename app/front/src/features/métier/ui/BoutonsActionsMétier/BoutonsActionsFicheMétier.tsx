import { type BoutonsActionsFicheMétierProps } from "./BoutonsActionsMétier.interface";
import useBoutonsActionsMétier from "./useBoutonsActionsMétier";
import Bouton from "@/components/Bouton/Bouton";
import { i18n } from "@/configuration/i18n/i18n";

const BoutonsActionsFicheMétier = ({ métier, taille }: BoutonsActionsFicheMétierProps) => {
  const { estFavori, mettreÀJourMétiersÉlève } = useBoutonsActionsMétier({
    métier,
  });

  return (
    <div className="grid justify-start justify-items-start gap-4 sm:grid-flow-col">
      {!estFavori && (
        <Bouton
          auClic={() => mettreÀJourMétiersÉlève([métier.id])}
          icône={{ position: "gauche", classe: "fr-icon-heart-line" }}
          label={i18n.COMMUN.AJOUTER_À_MA_SÉLECTION}
          taille={taille}
          type="button"
        />
      )}
      {estFavori && (
        <>
          <div className="grid grid-flow-col items-center gap-2 font-medium text-[--artwork-minor-red-marianne]">
            <span
              aria-hidden="true"
              className="fr-icon-heart-fill"
            />
            {i18n.COMMUN.AJOUTÉ_À_MA_SÉLECTION}
          </div>
          <Bouton
            auClic={() => mettreÀJourMétiersÉlève([métier.id])}
            icône={{ position: "gauche", classe: "fr-icon-close-line" }}
            label={i18n.COMMUN.SUPPRIMER_DE_MA_SÉLECTION}
            taille={taille}
            type="button"
            variante="secondaire"
          />
        </>
      )}
    </div>
  );
};

export default BoutonsActionsFicheMétier;
