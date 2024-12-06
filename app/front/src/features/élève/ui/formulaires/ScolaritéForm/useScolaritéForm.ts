import { type UseScolaritéFormArgs } from "./ScolaritéForm.interface";
import { scolaritéValidationSchema } from "./ScolaritéForm.validation";
import useMoyenneScolaritéForm from "./useMoyenneScolaritéForm";
import { i18n } from "@/configuration/i18n/i18n";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveForm from "@/features/élève/ui/hooks/useÉlèveForm/useÉlèveForm";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { BacÉlève, ClasseÉlève } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { référentielDonnéesQueryOptions } from "@/features/référentielDonnées/ui/référentielDonnéesQueries";
import { SelectProps } from "@codegouvfr/react-dsfr/SelectNext";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo } from "react";

export default function useScolaritéForm({ àLaSoumissionDuFormulaireAvecSuccès }: UseScolaritéFormArgs) {
  const { data: référentielDonnées } = useQuery(référentielDonnéesQueryOptions);
  const { élève } = useÉlève();
  const { mettreÀJourSpécialitésÉlève, mettreÀJourProfilÉlève } = useÉlèveMutation();
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

  const spécialitésBac = useMemo(
    () => référentielDonnées?.bacs.find((bac) => bac.id === valeurBac)?.spécialités ?? [],
    [valeurBac],
  );

  useEffect(() => {
    if (valeurBac) void mettreÀJourProfilÉlève({ bac: valeurBac });
  }, [valeurBac]);

  // Garder synchronisé la valeur react-hook-form et le profil de l'élève
  useEffect(() => {
    setValue("spécialités", élève?.spécialités ?? []);
  }, [élève?.spécialités]);

  useEffect(() => {
    const supprimerSpécialitésÉlèveInexistantesDansLeBacSélectionné = async () => {
      const idsSpécialitésÉlèveNonExistantesDansLeBac =
        // eslint-disable-next-line sonarjs/no-nested-functions
        élève?.spécialités?.filter((idSpécialité) => !spécialitésBac.some(({ id }) => id === idSpécialité)) ?? [];

      if (idsSpécialitésÉlèveNonExistantesDansLeBac.length > 0)
        await mettreÀJourSpécialitésÉlève(idsSpécialitésÉlèveNonExistantesDansLeBac);
    };

    void supprimerSpécialitésÉlèveInexistantesDansLeBacSélectionné();
  }, [spécialitésBac]);

  const moyenneScolaritéForm = useMoyenneScolaritéForm({
    référentielDonnées,
    watch,
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
    spécialitésBac,
    ...moyenneScolaritéForm,
  };
}
