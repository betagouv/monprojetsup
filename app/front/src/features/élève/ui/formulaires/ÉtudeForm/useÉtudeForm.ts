import { type AlternanceOptions, type DuréeÉtudesPrévueOptions, type useÉtudeFormArgs } from "./ÉtudeForm.interface";
import { étudeValidationSchema } from "./ÉtudeForm.validation";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";

export default function useÉtudeForm({ àLaSoumissionDuFormulaireAvecSuccès }: useÉtudeFormArgs) {
  const { register, erreurs, mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: étudeValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const duréeÉtudesPrévueOptions: DuréeÉtudesPrévueOptions = [
    {
      valeur: "options_ouvertes",
      label: i18n.ÉLÈVE.ÉTUDES.DURÉE_ETUDES.OPTIONS.OPTIONS_OUVERTES.LABEL,
    },
    {
      valeur: "courte",
      label: i18n.ÉLÈVE.ÉTUDES.DURÉE_ETUDES.OPTIONS.COURTE.LABEL,
    },
    {
      valeur: "longue",
      label: i18n.ÉLÈVE.ÉTUDES.DURÉE_ETUDES.OPTIONS.LONGUE.LABEL,
    },
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.ÉTUDES.DURÉE_ETUDES.OPTIONS.AUCUNE_IDÉE.LABEL,
    },
  ];

  const alternanceOptions: AlternanceOptions = [
    {
      valeur: "pas_interesse",
      label: i18n.ÉLÈVE.ÉTUDES.ALTERNANCE.OPTIONS.PAS_INTÉRESSÉ.LABEL,
    },
    {
      valeur: "indifferent",
      label: i18n.ÉLÈVE.ÉTUDES.ALTERNANCE.OPTIONS.INDIFFÉRENT.LABEL,
    },
    {
      valeur: "interesse",
      label: i18n.ÉLÈVE.ÉTUDES.ALTERNANCE.OPTIONS.INTÉRESSÉ.LABEL,
    },
    {
      valeur: "tres_interesse",
      label: i18n.ÉLÈVE.ÉTUDES.ALTERNANCE.OPTIONS.TRÈS_INTÉRESSÉ.LABEL,
    },
  ];

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    duréeÉtudesPrévueOptions,
    alternanceOptions,
  };
}
