import { type CarteFormationProps } from "./CarteFormation.interface";
import Badge from "@/components/_dsfr/Badge/Badge";
import Tag from "@/components/_dsfr/Tag/Tag";
import { i18n } from "@/configuration/i18n/i18n";

const CarteFormation = ({ id, nom, métiersAccessibles, affinité, sélectionnée = false }: CarteFormationProps) => {
  const NOMBRE_MÉTIERS_À_AFFICHER = 3;

  const classEnFonctionDeLaSelection = () => {
    if (sélectionnée) return "border-2 border-solid border-[--border-active-blue-france]";
    return "";
  };

  return (
    <div className={`fr-p-4w max-w-[470px] bg-[--background-default-grey] shadow-md ${classEnFonctionDeLaSelection()}`}>
      <div className="fr-grid-row items-center justify-between">
        <Badge
          taille="sm"
          titre={i18n.COMMUN.FORMATION}
          type="succès"
        />
        {affinité && (
          <p className="fr-text--sm fr-mb-0 text-[--text-default-success]">
            {i18n.COMMUN.TAUX_AFFINITÉ} {affinité * 100}%
          </p>
        )}
      </div>
      <h2 className="fr-h4 fr-mt-3v fr-mb-0 text-[--text-title-grey]">
        {nom}
        {id}
      </h2>
      {métiersAccessibles.length > 0 && (
        <>
          <p className="fr-text--sm fr-mb-3v fr-mt-3w text-[--text-label-grey]">
            {i18n.PAGE_RECHERCHE.EXEMPLES_MÉTIERS}
          </p>
          <ul className="m-0 flex list-none flex-wrap justify-start gap-2 p-0">
            {métiersAccessibles.slice(0, NOMBRE_MÉTIERS_À_AFFICHER).map((métier) => (
              <li key={métier.id}>
                <Tag
                  libellé={métier.nom}
                  taille="sm"
                />
              </li>
            ))}
            {métiersAccessibles.length > NOMBRE_MÉTIERS_À_AFFICHER && (
              <li>
                <Tag
                  libellé={`+${(métiersAccessibles.length - NOMBRE_MÉTIERS_À_AFFICHER).toString()}`}
                  taille="sm"
                />
              </li>
            )}
          </ul>
        </>
      )}
    </div>
  );
};

export default CarteFormation;
