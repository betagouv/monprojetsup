import { type CarteFormationProps } from "./CarteFormation.interface";
import Carte from "@/components/Carte/Carte";
import Tag from "@/components/Tag/Tag";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { useQuery } from "@tanstack/react-query";

const CarteFormation = ({
  id,
  titre,
  métiersAccessibles,
  affinité,
  communes,
  sélectionnée = false,
  auClic,
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
      auClic={auClic}
      estFavori={estUneFormationFavorite()}
      estMasqué={estUneFormationMasquée()}
      sélectionnée={sélectionnée}
      titre={titre}
    >
      {affinité && affinité > 0 ? (
        <div className="grid grid-flow-col justify-start gap-2">
          <span
            aria-hidden="true"
            className="fr-icon-checkbox-fill fr-icon--sm text-[--background-flat-success]"
          />
          <p className="fr-text--sm mb-0 text-[--text-label-green-emeraude]">
            {affinité} {i18n.CARTE_FORMATION.POINTS_AFFINITÉ}
          </p>
        </div>
      ) : null}
      {communes.length > 0 && (
        <div className="grid grid-flow-col justify-start gap-2">
          <span
            aria-hidden="true"
            className="fr-icon-map-pin-2-fill fr-icon--sm"
          />
          <p className="fr-text--sm mb-0">
            {i18n.CARTE_FORMATION.VILLES_PROPOSANT_FORMATION}{" "}
            <strong>
              {communes.length} {i18n.CARTE_FORMATION.VILLES_PROPOSANT_FORMATION_SUITE}
            </strong>
          </p>
        </div>
      )}
      {métiersAccessibles.length > 0 && (
        <div className="grid gap-3">
          <p className="fr-text--sm mb-0 text-[--text-label-grey]">{i18n.CARTE_FORMATION.MÉTIERS_ACCESSIBLES}</p>
          <ul className="m-0 flex list-none flex-wrap justify-start gap-2 p-0">
            {métiersAccessibles.slice(0, NOMBRE_MÉTIERS_À_AFFICHER).map((métier) => (
              <li key={métier.id}>
                <Tag
                  libellé={métier.nom}
                  taille="petit"
                />
              </li>
            ))}
            {métiersAccessibles.length > NOMBRE_MÉTIERS_À_AFFICHER && (
              <li>
                <Tag
                  libellé={`+${(métiersAccessibles.length - NOMBRE_MÉTIERS_À_AFFICHER).toString()}`}
                  taille="petit"
                />
              </li>
            )}
          </ul>
        </div>
      )}
    </Carte>
  );
};

export default CarteFormation;
