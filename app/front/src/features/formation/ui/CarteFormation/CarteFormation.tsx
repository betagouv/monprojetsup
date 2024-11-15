import { type CarteFormationProps } from "./CarteFormation.interface";
import Carte from "@/components/Carte/Carte";
import { i18n } from "@/configuration/i18n/i18n";
import CommunesProposantLaFormation from "@/features/formation/ui/CommunesProposantLaFormation/CommunesProposantLaFormation";
import NombreAffinité from "@/features/formation/ui/NombreAffinité/NombreAffinité";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { Tag } from "@codegouvfr/react-dsfr/Tag";
import { useQuery } from "@tanstack/react-query";

const CarteFormation = ({
  id,
  titre,
  métiersAccessibles,
  affinité,
  communes,
  sélectionnée = false,
}: CarteFormationProps) => {
  const { data: élève } = useQuery(élèveQueryOptions);

  const NOMBRE_MÉTIERS_À_AFFICHER = 3;

  const estUneFormationFavorite = () => {
    return élève?.formationsFavorites?.some((formationFavorite) => formationFavorite.id === id) ?? false;
  };

  const estUneFormationMasquée = () => {
    return élève?.formationsMasquées?.includes(id) ?? false;
  };

  return (
    <Carte
      estFavori={estUneFormationFavorite()}
      estMasqué={estUneFormationMasquée()}
      id={id}
      sélectionnée={sélectionnée}
      titre={titre}
    >
      <NombreAffinité affinité={affinité} />
      <CommunesProposantLaFormation communes={communes} />
      {métiersAccessibles.length > 0 && (
        <div className="grid gap-3">
          <p className="fr-text--sm mb-0 text-[--text-label-grey]">{i18n.CARTE_FORMATION.MÉTIERS_ACCESSIBLES}</p>
          <ul className="m-0 flex list-none flex-wrap justify-start gap-2 p-0">
            {métiersAccessibles.slice(0, NOMBRE_MÉTIERS_À_AFFICHER).map((métier) => (
              <li key={métier.id}>
                <Tag small>{métier.nom}</Tag>
              </li>
            ))}
            {métiersAccessibles.length > NOMBRE_MÉTIERS_À_AFFICHER && (
              <li>
                <Tag small>{`+${(métiersAccessibles.length - NOMBRE_MÉTIERS_À_AFFICHER).toString()}`}</Tag>
              </li>
            )}
          </ul>
        </div>
      )}
    </Carte>
  );
};

export default CarteFormation;
