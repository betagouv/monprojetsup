import { type CarteMétierProps } from "./CarteMétier.interface";
import Badge from "@/components/_dsfr/Badge/Badge";
import { i18n } from "@/configuration/i18n/i18n";

const CarteMétier = ({ id, nom, formations, sélectionnée = false }: CarteMétierProps) => {
  const classEnFonctionDeLaSelection = () => {
    if (sélectionnée) return "border-2 border-solid border-[--border-active-blue-france]";
    return "";
  };

  return (
    <div className={`fr-p-4w max-w-[470px] bg-[--background-default-grey] shadow-md ${classEnFonctionDeLaSelection()}`}>
      <div className="fr-grid-row items-center justify-between">
        <Badge
          taille="sm"
          titre={i18n.COMMUN.MÉTIER}
          type="alerte"
        />
      </div>
      <h2 className="fr-h4 fr-mt-3v fr-mb-0 text-[--text-title-grey]">
        {nom}
        {id}
      </h2>
      {formations.length > 0 && (
        <p className="fr-text--sm fr-mb-3v fr-mt-3w text-[--text-label-grey]">
          {formations.length} {i18n.PAGE_RECHERCHE.FORMATIONS_POUR_APPRENDRE_METIER}
        </p>
      )}
    </div>
  );
};

export default CarteMétier;
