/* eslint-disable react-hooks/rules-of-hooks */
import { type ÉlèveInformationsModifiables, type UseÉlèveFormArgs } from "./useÉlèveForm.interface";
import { dépendances } from "@/configuration/dépendances/dépendances";
import { queryClient } from "@/configuration/lib/tanstack-query";
import { élèveQueryOptions } from "@/features/élève/ui/options";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { type SubmitHandler, useForm } from "react-hook-form";

export default function useÉlèveForm({ schémaValidation, àLaSoumissionDuFormulaireAvecSuccès }: UseÉlèveFormArgs) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ÉlèveInformationsModifiables>({
    resolver: zodResolver(schémaValidation),
    defaultValues: queryClient.getQueryData(élèveQueryOptions.queryKey),
  });

  const mutationÉlève = useMutation({
    mutationFn: async (données: ÉlèveInformationsModifiables) => {
      return await dépendances.mettreÀJourÉlèveUseCase.run(données);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: élèveQueryOptions.queryKey });
    },
  });

  const mettreÀJourÉlève: SubmitHandler<ÉlèveInformationsModifiables> = async (données) => {
    await mutationÉlève.mutateAsync(données);
    àLaSoumissionDuFormulaireAvecSuccès?.();
  };

  return {
    register,
    erreurs: errors,
    mettreÀJourÉlève: handleSubmit(mettreÀJourÉlève),
  };
}
