import {
  type ContenuModale,
  type MétiersAccessiblesFicheFormationProps,
} from "./MétiersAccessiblesFicheFormation.interface";
import Bouton from "@/components/Bouton/Bouton";
import BoutonsFavorisCorbeille from "@/components/BoutonsFavorisCorbeille/BoutonsFavorisCorbeille";
import ListeLiensExternesSousFormeBouton from "@/components/ListeLiensExternesSousFormeBouton/ListeLiensExternesSousFormeBouton";
import Modale from "@/components/Modale/Modale";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { useState } from "react";

const MétiersAccessiblesFicheFormation = ({ métiers }: MétiersAccessiblesFicheFormationProps) => {
  const [contenuModale, setContenuModale] = useState<ContenuModale>();

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
        {métiers.map((métier) => (
          <li key={métier.id}>
            <Bouton
              ariaControls={idModale}
              auClic={() =>
                setContenuModale({
                  titre: métier.nom,
                  contenu: métier.descriptif,
                  liens: métier.liens,
                })
              }
              dataFrOpened="false"
              label={métier.nom}
              taille="petit"
              type="button"
              variante="tertiaire"
            />
          </li>
        ))}
      </ul>
      <Modale
        boutons={<BoutonsFavorisCorbeille />}
        id={idModale}
        titre={contenuModale?.titre ?? ""}
      >
        <div className="grid gap-6">
          <p className="mb-0">{contenuModale?.contenu ?? null}</p>
          <ListeLiensExternesSousFormeBouton liens={contenuModale?.liens ?? []} />
        </div>
      </Modale>
    </div>
  );
};

export default MétiersAccessiblesFicheFormation;
