import { type UseÉlèveFormArgs } from "./useÉlèveForm.interface";
import { type Élève } from "@/features/élève/domain/élève.interface";
import useÉlève from "@/features/élève/ui/hooks/useÉlève/useÉlève";
import useÉlèveMutation from "@/features/élève/ui/hooks/useÉlèveMutation/useÉlèveMutation";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

export default function useÉlèveForm({ schémaValidation, àLaSoumissionDuFormulaireAvecSuccès }: UseÉlèveFormArgs) {
  const { élève } = useÉlève();
  const { mettreÀJourProfilÉlève } = useÉlèveMutation({
    àLaMiseÀJourÉlèveAvecSuccès: àLaSoumissionDuFormulaireAvecSuccès,
  });

  const sauvegarder = async (profilÉlève: Élève) => {
    await mettreÀJourProfilÉlève(profilÉlève);
  };

  const {
    register,
    handleSubmit,
    watch,
    getValues,
    setValue,
    formState: { errors, dirtyFields },
  } = useForm<Élève>({
    resolver: schémaValidation ? zodResolver(schémaValidation) : undefined,
    defaultValues: élève ?? undefined,
  });

  return {
    register,
    watch,
    getValues,
    setValue,
    dirtyFields,
    erreurs: errors,
    handleSubmit,
    mettreÀJourÉlève: handleSubmit(sauvegarder),
    élève,
  };
}
