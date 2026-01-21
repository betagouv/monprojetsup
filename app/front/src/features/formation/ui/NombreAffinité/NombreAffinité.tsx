import { i18n } from "@/configuration/i18n/i18n.ts";
import { NombreAffinitéProps } from "@/features/formation/ui/NombreAffinité/NombreAffinité.interface.tsx";

const NombreAffinité = ({ affinité }: NombreAffinitéProps) => {
  if (!affinité || affinité === 0) return null;

  return (
    <div className="grid grid-flow-col justify-start gap-2">
      <span
        aria-hidden="true"
        className="fr-icon-checkbox-fill fr-icon--sm text-[--background-flat-success]"
      />
      <p className="fr-text--sm mb-0 text-[--text-label-green-emeraude]">
        {affinité}{" "}
        {affinité < 2 ? i18n.CARTE_FORMATION.POINTS_AFFINITÉ_SINGULIER : i18n.CARTE_FORMATION.POINTS_AFFINITÉ_PLURIEL}
      </p>
    </div>
  );
};

export default NombreAffinité;
