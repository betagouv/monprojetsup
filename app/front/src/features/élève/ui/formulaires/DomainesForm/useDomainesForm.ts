import { type useDomainesFormArgs } from "./DomainesForm.interface";
import { domainesValidationSchema } from "./DomainesForm.validation";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { useQuery } from "@tanstack/react-query";
import { useId } from "react";

export default function useDomainesForm({ àLaSoumissionDuFormulaireAvecSuccès }: useDomainesFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const légendeId = useId();

  const { setValue, mettreÀJourÉlève, getValues, erreurs } = useÉlèveForm({
    schémaValidation: domainesValidationSchema,
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const filtresGroupésParCatégories =
    référentielDonnées?.domainesProfessionnels.map((catégorie) => ({
      nom: catégorie.nom,
      emoji: catégorie.emoji,
      filtres: catégorie.sousCatégoriesdomainesProfessionnels,
    })) ?? [];

  const auChangementFiltresSélectionnés = (filtreIdsSélectionnés: string[]) =>
    setValue("domaines", filtreIdsSélectionnés);

  return {
    mettreÀJourÉlève,
    erreurs,
    filtresGroupésParCatégories,
    filtreIdsSélectionnésParDéfaut: getValues("domaines") ?? [],
    auChangementFiltresSélectionnés,
    légendeId,
  };
}
