import { type ProjetFormInputs, type ProjetFormProps, type SituationOptions } from "./ProjetForm.interface";
import { projetValidationSchema } from "./ProjetForm.validation";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import { dependencies } from "@/configuration/dependencies/dependencies";
import { i18n } from "@/configuration/i18n/i18n";
import { MettreÀJourÉlèveUseCase } from "@/features/élève/usecase/MettreÀJourÉlève";
import { zodResolver } from "@hookform/resolvers/zod";
import { type SubmitHandler, useForm } from "react-hook-form";

const ProjetForm = ({ valeursParDéfaut, àLaSoumissionDuFormulaireAvecSuccès, formId }: ProjetFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProjetFormInputs>({
    resolver: zodResolver(projetValidationSchema),
    defaultValues: {
      situation: valeursParDéfaut?.situation,
    },
  });

  const onSubmit: SubmitHandler<ProjetFormInputs> = async (données) => {
    const élèveMisÀJour = await new MettreÀJourÉlèveUseCase(dependencies.élèveRepository).run(données);
    if (élèveMisÀJour) àLaSoumissionDuFormulaireAvecSuccès?.();
  };

  const situationOptions: SituationOptions = [
    {
      valeur: "aucune_idee",
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
    },
    {
      valeur: "quelques_pistes",
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.QUELQUES_PISTES.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
    },
    {
      valeur: "projet_precis",
      label: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.PROJET_PRÉCIS.LABEL,
      description: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.PROJET_PRÉCIS.DESCRIPTION,
      pictogramme: i18n.ÉLÈVE.PROJET.SITUATION.OPTIONS.PROJET_PRÉCIS.EMOJI,
    },
  ];

  return (
    <form
      id={formId}
      onSubmit={handleSubmit(onSubmit)}
    >
      <BoutonRadioRiche
        légende={i18n.ÉLÈVE.PROJET.SITUATION.LÉGENDE}
        options={situationOptions}
        registerHookForm={register("situation")}
        status={errors.situation ? { type: "erreur", message: errors.situation.message } : undefined}
      />
    </form>
  );
};

export default ProjetForm;
