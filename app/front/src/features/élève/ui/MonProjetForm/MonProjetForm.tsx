import { type MaSituationOptions, type MonProjetFormInputs, type MonProjetFormProps } from "./MonProjetForm.interface";
import { monProjetValidationSchema } from "./MonProjetForm.validation";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import { i18n } from "@/configuration/i18n/i18n";
import { zodResolver } from "@hookform/resolvers/zod";
import { type SubmitHandler, useForm } from "react-hook-form";

const MonProjetForm = ({ valeursParDéfaut, àLaSoumissionDuFormulaireAvecSuccès, formId }: MonProjetFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<MonProjetFormInputs>({
    resolver: zodResolver(monProjetValidationSchema),
    defaultValues: {
      maSituation: valeursParDéfaut?.maSituation,
    },
  });

  const onSubmit: SubmitHandler<MonProjetFormInputs> = (data) => {
    // eslint-disable-next-line no-console
    console.log(data);
    àLaSoumissionDuFormulaireAvecSuccès?.();
  };

  const maSituationOptions: MaSituationOptions = [
    {
      valeur: "aucune_idee",
      label: i18n.MON_PROJET.MA_SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
      description: i18n.MON_PROJET.MA_SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
      pictogramme: i18n.MON_PROJET.MA_SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
    },
    {
      valeur: "quelques_pistes",
      label: i18n.MON_PROJET.MA_SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
      description: i18n.MON_PROJET.MA_SITUATION.OPTIONS.QUELQUES_PISTES.DESCRIPTION,
      pictogramme: i18n.MON_PROJET.MA_SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
    },
    {
      valeur: "projet_precis",
      label: i18n.MON_PROJET.MA_SITUATION.OPTIONS.PROJET_PRÉCIS.LABEL,
      description: i18n.MON_PROJET.MA_SITUATION.OPTIONS.PROJET_PRÉCIS.DESCRIPTION,
      pictogramme: i18n.MON_PROJET.MA_SITUATION.OPTIONS.PROJET_PRÉCIS.EMOJI,
    },
  ];

  return (
    <form
      id={formId}
      onSubmit={handleSubmit(onSubmit)}
    >
      <BoutonRadioRiche
        légende={i18n.MON_PROJET.MA_SITUATION.LÉGENDE}
        options={maSituationOptions}
        registerHookForm={register("maSituation")}
        status={errors.maSituation ? { type: "erreur", message: errors.maSituation.message } : undefined}
      />
    </form>
  );
};

export default MonProjetForm;
