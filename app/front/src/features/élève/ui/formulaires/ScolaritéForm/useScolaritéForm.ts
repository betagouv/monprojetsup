import { type UseScolaritéFormArgs } from "./ScolaritéForm.interface";
import { scolaritéValidationSchema } from "./ScolaritéForm.validation";
import useMoyenneScolaritéForm from "./useMoyenneScolaritéForm";
import useSpécialitésScolaritéForm from "./useSpécialitésScolaritéForm";
import { i18n } from "@/configuration/i18n/i18n";
import { BacÉlève, ClasseÉlève } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import { SelectProps } from "@codegouvfr/react-dsfr/SelectNext";
import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";

export default function useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseScolaritéFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);

  const { register, erreurs, mettreÀJourÉlève, watch, setValue, getValues } = useÉlèveForm({
    schémaValidation: scolaritéValidationSchema(référentielDonnées?.bacs ?? []),
    àLaSoumissionDuFormulaireAvecSuccès,
  });

  const valeurBac = watch("bac");

  const classeOptions: SelectProps.Option<ClasseÉlève>[] = useMemo(
    () =>
      référentielDonnées?.élève.classes.map((classe) => ({
        value: classe,
        label: i18n.ÉLÈVE.SCOLARITÉ.CLASSE.OPTIONS[classe].LABEL,
      })) ?? [],
    [référentielDonnées],
  );

  const bacOptions: SelectProps.Option<NonNullable<BacÉlève>>[] = useMemo(
    () =>
      référentielDonnées?.bacs
        .filter((bac) => bac.id !== "NC")
        .map((bac) => ({
          value: bac.id,
          label: bac.nom,
        })) ?? [],
    [référentielDonnées],
  );

  const moyenneScolaritéForm = useMoyenneScolaritéForm({
    référentielDonnées,
    watch,
    setValue,
    getValues,
  });

  const spécialitésScolaritéForm = useSpécialitésScolaritéForm({
    référentielDonnées,
    valeurBac,
    setValue,
    getValues,
  });

  return {
    mettreÀJourÉlève,
    erreurs,
    register,
    classeOptions,
    bacOptions,
    valeurBac,
    ...moyenneScolaritéForm,
    ...spécialitésScolaritéForm,
  };
}
