import BoutonRadioRiche from "@/components/_dsfr/BoutonRadioRiche/BoutonRadioRiche";
import { i18n } from "@/configuration/i18n/i18n";

const InscriptionMonProjet = () => {
  return (
    <div>
      <h1 className="fr-h2">{i18n.INSCRIPTION.TITRES.MON_PROJET}</h1>
      <p className="fr-text--lg">{i18n.COMMUN.CHAMPS_OBLIGATOIRES}</p>
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
      />
      <hr />
    </div>
  );
};

export default InscriptionMonProjet;
