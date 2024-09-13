import { type CarteFormationProps } from "./CarteFormation.interface";
import Tag from "@/components/_dsfr/Tag/Tag";
import Titre from "@/components/Titre/Titre";
import { i18n } from "@/configuration/i18n/i18n";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { actionsFicheFormationStore } from "@/features/formation/ui/store/useFicheFormation/useFicheFormation";
import { useQuery } from "@tanstack/react-query";

const CarteFormation = ({
  id,
  nom,
  métiersAccessibles,
  affinité,
  communes,
  sélectionnée = false,
}: CarteFormationProps) => {
  const { changerFormationAffichéeId } = actionsFicheFormationStore();
  const { data: élève } = useQuery(élèveQueryOptions);

  const NOMBRE_MÉTIERS_À_AFFICHER = 3;

  const classEnFonctionDeLaSélection = () => {
    if (sélectionnée) return "border-[--border-active-blue-france]";
    return "border-transparent";
  };

  const estUneFormationFavorite = () => {
    return élève?.formationsFavorites?.some((formationFavorite) => formationFavorite.id === id) ?? false;
  };

  const estUneFormationMasquée = () => {
    return élève?.formationsMasquées?.includes(id) ?? false;
  };

  return (
    <button
      className={`grid max-w-[550px] gap-4 border-2 border-solid bg-[--background-default-grey] p-6 text-left shadow-md ${classEnFonctionDeLaSélection()}`}
      onClick={() => changerFormationAffichéeId(id)}
      type="button"
    >
      <div className="grid grid-flow-col items-baseline justify-between gap-1">
        <div className="*:mb-0">
          <Titre
            niveauDeTitre="h2"
            styleDeTitre="h4"
          >
            {nom}
          </Titre>
        </div>
        {estUneFormationFavorite() && (
          <div>
            <span
              aria-hidden="true"
              className="fr-icon-heart-fill fr-icon--sm rounded bg-[--background-contrast-error] px-1 text-[--text-default-error]"
            />
            <span className="sr-only">{i18n.ACCESSIBILITÉ.FAVORIS}</span>
          </div>
        )}
        {estUneFormationMasquée() && (
          <div>
            <span
              aria-hidden="true"
              className="fr-icon-eye-off-line fr-icon--sm rounded bg-[--background-alt-beige-gris-galet] px-1 text-[--text-mention-grey]"
            />
            <span className="sr-only">{i18n.ACCESSIBILITÉ.MASQUÉ}</span>
          </div>
        )}
      </div>
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
            {i18n.CARTE_FORMATION.FORMATION_DISPONIBLES}{" "}
            <strong>
              {communes.length} {i18n.CARTE_FORMATION.FORMATION_DISPONIBLES_SUITE}
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
    </button>
  );
};

export default CarteFormation;
