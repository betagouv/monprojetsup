import { type MétiersAccessiblesFicheFormationProps } from "./MétiersAccessiblesFicheFormation.interface";
import useMétiersAccessiblesFicheFormation from "./useMétiersAccessiblesFicheFormation";
import Bouton from "@/components/Bouton/Bouton";
import Titre from "@/components/Titre/Titre";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import ModaleMétier from "@/features/métier/ui/ModaleMétier/ModaleMétier";

const MétiersAccessiblesFicheFormation = ({ métiers }: MétiersAccessiblesFicheFormationProps) => {
  const { estMétierFavoriPourÉlève } = useÉlève();
  const { modaleMétier, métiersTriésParFavoris, métierSélectionné, setMétierSélectionné } =
    useMétiersAccessiblesFicheFormation({ métiers });

  if (métiers.length === 0) return null;

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
        {métiersTriésParFavoris.slice(0, constantes.FORMATIONS.FICHES.NB_MÉTIERS_À_AFFICHER).map((métier) => (
          <li key={métier.id}>
            <Bouton
              auClic={() => {
                modaleMétier.open();
                setMétierSélectionné(métier);
              }}
              icône={
                estMétierFavoriPourÉlève(métier.id) ? { position: "gauche", classe: "fr-icon-heart-fill" } : undefined
              }
              label={métier.nom}
              taille="petit"
              type="button"
              variante="tertiaire"
            />
          </li>
        ))}
      </ul>
      <ModaleMétier
        métier={métierSélectionné}
        modale={modaleMétier}
      />
    </div>
  );
};

export default MétiersAccessiblesFicheFormation;
