import { type MétiersAccessiblesFicheFormationProps } from "./MétiersAccessiblesFicheFormation.interface";
import Bouton from "@/components/Bouton/Bouton";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";
import Modale from "@/components/Modale/Modale";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import BoutonsActionsModalMétier from "@/features/métier/ui/BoutonsActionsMétier/BoutonsActionsModalMétier";
import { useState } from "react";

const MétiersAccessiblesFicheFormation = ({ métiers }: MétiersAccessiblesFicheFormationProps) => {
  const NOMBRE_MÉTIERS_À_AFFICHER = 10;

  const [métierSélectionné, setMétierSélectionné] = useState<MétiersAccessiblesFicheFormationProps["métiers"][number]>(
    métiers[0],
  );

  if (métiers.length === 0) return null;

  const idModale = "modale-métier";

  return (
    <div>
      <div className="*:mb-4">
        <Titre
          niveauDeTitre="h2"
          styleDeTitre="text--lead"
        >
          {i18n.PAGE_FORMATION.EXEMPLES_MÉTIERS_ACCESSIBLES}
        </Titre>
      </div>
      <ul className="flex list-none flex-wrap justify-start gap-1 p-0">
        {métiers.slice(0, NOMBRE_MÉTIERS_À_AFFICHER).map((métier) => (
          <li key={métier.id}>
            <Bouton
              ariaControls={idModale}
              auClic={() => setMétierSélectionné(métier)}
              dataFrOpened="false"
              label={métier.nom}
              taille="petit"
              type="button"
              variante="tertiaire"
            />
          </li>
        ))}
      </ul>
      {métierSélectionné && (
        <Modale
          boutons={
            <BoutonsActionsModalMétier
              ariaControls={idModale}
              métier={{ ...métierSélectionné, formations: [] }}
              taille="grand"
            />
          }
          id={idModale}
          titre={métierSélectionné?.nom ?? ""}
        >
          <div className="grid gap-6">
            <p className="mb-0">{métierSélectionné?.descriptif ?? null}</p>
            <ListeLiensExternesSousFormeBouton liens={métierSélectionné?.liens ?? []} />
          </div>
        </Modale>
      )}
    </div>
  );
};

export default MétiersAccessiblesFicheFormation;
