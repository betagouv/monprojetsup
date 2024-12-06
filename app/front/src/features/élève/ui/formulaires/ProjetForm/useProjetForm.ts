import { type SituationOptions, type UseProjetFormArgs } from "./ProjetForm.interface";
import { projetValidationSchema } from "./ProjetForm.validation";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";

export default function useProjetForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseProjetFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const { register, erreurs, mettreÀJourÉlève } = useÉlèveForm({
    schémaValidation: projetValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const situationOptions: SituationOptions =
    référentielDonnées?.élève.situations.map((situation) => ({
      valeur: situation,
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS[situation].LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS[situation].DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS[situation].EMOJI,
    })) ?? [];

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    situationOptions,
  };
}
