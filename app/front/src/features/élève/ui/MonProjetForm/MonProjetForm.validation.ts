import { i18n } from "@/configuration/i18n/i18n";
import { z } from "zod";

export const monProjetValidationSchema = z.object({
  maSituation: z.enum(["aucune_idee", "quelques_pistes", "projet_precis"], {
    required_error: i18n.MON_PROJET.MA_SITUATION.ERREUR_FORMULAIRE,
    invalid_type_error: i18n.MON_PROJET.MA_SITUATION.ERREUR_FORMULAIRE,
  }),
});
