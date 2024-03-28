import Bouton from "@/components/_dsfr/Bouton/Bouton";
import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import { i18n } from "@/configuration/i18n/i18n";
import { type SubmitHandler, useForm } from "react-hook-form";

type Inputs = {
  maSituation: string;
};

const onSubmit: SubmitHandler<Inputs> = (data) => console.log(data);

const InscriptionMonProjet = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Inputs>();

  return (
    <>
      <h1 className="fr-h2 fr-mb-1w">{i18n.INSCRIPTION.TITRES.MON_PROJET}</h1>
      <p className="fr-text--lg">{i18n.COMMUN.CHAMPS_OBLIGATOIRES}</p>
      <form onSubmit={handleSubmit(onSubmit)}>
        <BoutonRadioRiche
          légende={i18n.MON_PROJET.MA_SITUATION.LÉGENDE}
          options={[
            {
              id: "aucune_idee",
              label: i18n.MON_PROJET.MA_SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
              description: i18n.MON_PROJET.MA_SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
              pictogramme: i18n.MON_PROJET.MA_SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
            },
            {
              id: "quelques_pistes",
              label: i18n.MON_PROJET.MA_SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
              description: i18n.MON_PROJET.MA_SITUATION.OPTIONS.QUELQUES_PISTES.DESCRIPTION,
              pictogramme: i18n.MON_PROJET.MA_SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
            },
            {
              id: "projet_precis",
              label: i18n.MON_PROJET.MA_SITUATION.OPTIONS.PROJET_PRÉCIS.LABEL,
              description: i18n.MON_PROJET.MA_SITUATION.OPTIONS.PROJET_PRÉCIS.DESCRIPTION,
              pictogramme: i18n.MON_PROJET.MA_SITUATION.OPTIONS.PROJET_PRÉCIS.EMOJI,
            },
          ]}
          registerHookForm={register("maSituation")}
          status={errors.maSituation ? { type: "erreur", message: errors.maSituation.message } : undefined}
        />
        <hr />
        <div className="fr-grid-row fr-grid-row--right">
          <Bouton
            icône={{ position: "droite", classe: "fr-icon-arrow-right-line" }}
            label={i18n.INSCRIPTION.CONTINUER}
            type="submit"
          />
        </div>
      </form>
    </>
  );
};

export default InscriptionMonProjet;
