import { type UseÉlèveFormArgs } from "./useÉlèveForm.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

export default function useÉlèveForm({ schémaValidation, àLaSoumissionDuFormulaireAvecSuccès }: UseÉlèveFormArgs) {
  const { mettreÀJourÉlève, élève } = useÉlève({ àLaSoumissionDuFormulaireAvecSuccès });

  const {
    register,
    handleSubmit,
    watch,
    getValues,
    setValue,
    formState: { errors, dirtyFields },
  } = useForm<Élève>({
    resolver: zodResolver(schémaValidation),
    defaultValues: élève ?? undefined,
  });

  return {
    register,
    watch,
    getValues,
    setValue,
    dirtyFields,
    erreurs: errors,
    mettreÀJourÉlève: handleSubmit(mettreÀJourÉlève),
    élève,
  };
}
