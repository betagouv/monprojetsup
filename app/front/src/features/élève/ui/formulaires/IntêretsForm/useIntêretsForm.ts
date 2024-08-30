import { type useIntêretsFormArgs } from "./IntêretsForm.interface";
import { centresIntêretsValidationSchema } from "./IntêretsForm.validation";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { useQuery } from "@tanstack/react-query";
import { useId } from "react";

export default function useIntêretsForm({ àLaSoumissionDuFormulaireAvecSuccès }: useIntêretsFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const légendeId = useId();

  const { setValue, mettreÀJourÉlève, getValues, erreurs } = useÉlèveForm({
    schémaValidation: centresIntêretsValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories =
    référentielDonnées?.centresIntêrets.map((catégorie) => ({
      nom: catégorie.nom,
      emoji: catégorie.emoji,
      filtres: catégorie.sousCatégoriesCentreIntêret,
    })) ?? [];

  const auChangementFiltresSélectionnés = (filtreIdsSélectionnés: string[]) =>
    setValue("centresIntêrets", filtreIdsSélectionnés);

  return {
    mettreÀJourÉlève,
    erreurs,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut: getValues("centresIntêrets") ?? [],
    auChangementFiltresSélectionnés,
    légendeId,
  };
}
