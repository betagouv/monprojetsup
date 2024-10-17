import { i18n } from "@/configuration/i18n/i18n.ts";
import { NombreRaisonsDInteretProps } from "@/features/formation/ui/NombreRaisonsDInteret/NombreRaisonsDInteret.interface.tsx";

const NombreRaisonsDInteret = ({ affinité }: NombreRaisonsDInteretProps) => {
  return affinité && affinité > 0 ? (
    <div className="grid grid-flow-col justify-start gap-2">
      <span
        aria-hidden="true"
        className="fr-icon-checkbox-fill fr-icon--sm text-[--background-flat-success]"
      />
      <p className="fr-text--sm mb-0 text-[--text-label-green-emeraude]">
        {affinité} {i18n.CARTE_FORMATION.POINTS_AFFINITÉ}
      </p>
    </div>
  ) : null;
};

export default NombreRaisonsDInteret;
