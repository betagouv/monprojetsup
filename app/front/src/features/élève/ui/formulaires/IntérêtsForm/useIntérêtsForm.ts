import { type useIntérêtsFormArgs } from "./IntérêtsForm.interface";
import { centresIntérêtsValidationSchema } from "./IntérêtsForm.validation";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";
import { useId } from "react";

export default function useIntérêtsForm({ àLaSoumissionDuFormulaireAvecSuccès }: useIntérêtsFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const légendeId = useId();

  const { setValue, mettreÀJourÉlève, getValues, erreurs } = useÉlèveForm({
    schémaValidation: centresIntérêtsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories =
    référentielDonnées?.centresIntérêts.map((catégorie) => ({
      nom: catégorie.nom,
      emoji: catégorie.emoji,
      filtres: catégorie.sousCatégoriesCentreIntérêt,
    })) ?? [];

  const auChangementFiltresSélectionnés = (filtreIdsSélectionnés: string[]) =>
    setValue("centresIntérêts", filtreIdsSélectionnés);

  return {
    mettreÀJourÉlève,
    erreurs,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut: getValues("centresIntérêts") ?? [],
    auChangementFiltresSélectionnés,
    légendeId,
  };
}
