import { type useIntérêtsFormArgs } from "./IntérêtsForm.interface";
import { centresIntérêtsValidationSchema } from "./IntérêtsForm.validation";
import { actionsToastStore } from "@/components/Toast/useToast/useToast";
import { i18n } from "@/configuration/i18n/i18n";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useQuery } from "@tanstack/react-query";
import { useId } from "react";

export default function useIntérêtsForm({ àLaSoumissionDuFormulaireAvecSuccès }: useIntérêtsFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { déclencherToast } = actionsToastStore();

  const légendeId = useId();

  const { setValue, mettreÀJourÉlève, getValues, erreurs } = useÉlèveForm({
    schémaValidation: centresIntérêtsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories =
    référentielDonnées?.centresIntérêts.map((catégorie) => ({
      nom: catégorie.nom,
      emoji: catégorie.emoji,
      afficherDétail: catégorie.sousCatégoriesCentreIntérêt.some((sousCatégorie) => sousCatégorie.description !== null),
      filtres: catégorie.sousCatégoriesCentreIntérêt,
    })) ?? [];

  const auChangementFiltresSélectionnés = (filtreIdsSélectionnés: string[]) =>
    setValue("centresIntérêts", filtreIdsSélectionnés);

  if (erreurs.centresIntérêts?.message) {
    déclencherToast(i18n.COMMUN.ERREURS_FORMULAIRES.TITRE_GÉNÉRIQUE, erreurs.centresIntérêts.message, "error");
  }

  return {
    mettreÀJourÉlève,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut: getValues("centresIntérêts") ?? [],
    auChangementFiltresSélectionnés,
    légendeId,
  };
}
