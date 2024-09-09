/* eslint-disable react-hooks/rules-of-hooks */
import { type UseÉlèveFormArgs } from "./useÉlèveForm.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import { élèveQueryOptions } from "@/features/élève/ui/élèveQueries";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery } from "@tanstack/react-query";
import { type SubmitHandler, useForm } from "react-hook-form";

export default function useÉlèveForm({ schémaValidation, àLaSoumissionDuFormulaireAvecSuccès }: UseÉlèveFormArgs) {
  const { data: valeursParDéfaut } = useQuery(élèveQueryOptions);
  const mutationÉlève = useMutation<Élève, unknown, Élève>({ mutationKey: ["mettreÀJourÉlève"] });
  const {
    register,
    handleSubmit,
    watch,
    getValues,
    setValue,
    formState: { errors, dirtyFields },
  } = useForm<Élève>({
    resolver: zodResolver(schémaValidation),
    defaultValues: valeursParDéfaut ?? undefined,
  });

  const mettreÀJourÉlève: SubmitHandler<Élève> = async (données) => {
    await mutationÉlève.mutateAsync({ ...valeursParDéfaut, ...données });
    àLaSoumissionDuFormulaireAvecSuccès?.();
  };

  return {
    register,
    watch,
    getValues,
    setValue,
    dirtyFields,
    erreurs: errors,
    mettreÀJourÉlève: handleSubmit(mettreÀJourÉlève),
  };
}
