import { type MétiersAccessiblesFicheFormationProps } from "./MétiersAccessiblesFicheFormation.interface";
import Bouton from "@/components/Bouton/Bouton";
import { BoutonProps } from "@/components/Bouton/Bouton.interface.tsx";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";
import Modale from "@/components/Modale/Modale";
import Titre from "@/components/Titre/Titre";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import BoutonsActionsModalMétier from "@/features/métier/ui/BoutonsActionsMétier/BoutonsActionsModalMétier";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

const MétiersAccessiblesFicheFormation = ({ métiers }: MétiersAccessiblesFicheFormationProps) => {
  const { data: élève } = useQuery(élèveQueryOptions);

  const [métierSélectionné, setMétierSélectionné] = useState<MétiersAccessiblesFicheFormationProps["métiers"][number]>(
    métiers[0],
  );

  const afficherCoeurFavoris = (métierId: string): BoutonProps["icône"] => {
    if (élève?.métiersFavoris?.includes(métierId)) {
      return { position: "gauche", classe: "fr-icon-heart-fill" };
    }

    return undefined;
  };

  const métiersTriésParFavoris = [...métiers].sort((a, b) => {
    const aEstFavori = élève?.métiersFavoris?.includes(a.id);
    const bEstFavori = élève?.métiersFavoris?.includes(b.id);

    if (aEstFavori && !bEstFavori) {
      return -1;
    } else if (!aEstFavori && bEstFavori) {
      return 1;
    }

    return 0;
  });

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
        {métiersTriésParFavoris.slice(0, constantes.FICHE_FORMATION.NB_MÉTIERS_À_AFFICHER).map((métier) => (
          <li key={métier.id}>
            <Bouton
              ariaControls={idModale}
              auClic={() => setMétierSélectionné(métier)}
              dataFrOpened="false"
              icône={afficherCoeurFavoris(métier.id)}
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
            <p className="mb-0 whitespace-pre-line">{métierSélectionné?.descriptif ?? null}</p>
            <ListeLiensExternesSousFormeBouton liens={métierSélectionné?.liens ?? []} />
          </div>
        </Modale>
      )}
    </div>
  );
};

export default MétiersAccessiblesFicheFormation;
