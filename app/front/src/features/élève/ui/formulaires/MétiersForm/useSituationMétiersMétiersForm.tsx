import {
  SituationMétiersÉlève,
  StatusSituationMétiers,
  UseSituationMétiersMétiersFormArgs,
} from "./MétiersForm.interface";
import IllustrationBoutonRadioEmoji from "@/components/IllustrationBoutonRadioEmoji/IllustrationBoutonRadioEmoji";
import { constantes } from "@/configuration/constantes";
import { i18n } from "@/configuration/i18n/i18n";
import { RadioButtonsProps } from "@codegouvfr/react-dsfr/RadioButtons";
import { useMemo, useState } from "react";

export default function useSituationMétiersMétiersForm({ getValues, setValue }: UseSituationMétiersMétiersFormArgs) {
  const optionSélectionnéeAuChargement = (): SituationMétiersÉlève | null => {
    const métiersFavoris = getValues(constantes.MÉTIERS.CHAMP_MÉTIERS_FAVORIS);
    return métiersFavoris && métiersFavoris.length > 0 ? "quelques_pistes" : null;
  };

  const [optionSélectionnée, setOptionSélectionnée] = useState<SituationMétiersÉlève | null>(
    optionSélectionnéeAuChargement,
  );

  const [status, setStatus] = useState<StatusSituationMétiers>({
    type: undefined,
    message: undefined,
  });

  const options = useMemo(
    (): RadioButtonsProps["options"] =>
      [
        {
          valeur: "quelques_pistes" as const,
          label: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.LABEL,
          emoji: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.QUELQUES_PISTES.EMOJI,
        },
        {
          valeur: "aucune_idee" as const,
          label: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.LABEL,
          description: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.DESCRIPTION,
          emoji: i18n.ÉLÈVE.MÉTIERS.SITUATION.OPTIONS.AUCUNE_IDÉE.EMOJI,
        },
      ].map((option) => ({
        nativeInputProps: {
          value: option.valeur,
          defaultChecked: optionSélectionnée === option.valeur,
          onChange: () => {
            if (option.valeur === "aucune_idee") {
              setValue(constantes.MÉTIERS.CHAMP_MÉTIERS_FAVORIS, []);
            }

            setOptionSélectionnée(option.valeur);
            setStatus({ type: undefined, message: undefined });
          },
        },
        label: option.label,
        hintText: option.description,
        illustration: <IllustrationBoutonRadioEmoji emoji={option.emoji} />,
      })),
    [optionSélectionnée, setValue],
  );

  return {
    options,
    status,
    modifierStatus: setStatus,
    optionSélectionnée,
  };
}
