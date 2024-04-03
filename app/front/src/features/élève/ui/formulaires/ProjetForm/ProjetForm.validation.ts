import { i18n } from "@/configuration/i18n/i18n";
import { situationÉlève } from "@/features/élève/domain/élève.interface";
import { z } from "zod";

export const projetValidationSchema = z.object({
  situation: z.enum(situationÉlève, {
    required_error: i18n.ÉLÈVE.PROJET.SITUATION.ERREUR_FORMULAIRE,
    invalid_type_error: i18n.ÉLÈVE.PROJET.SITUATION.ERREUR_FORMULAIRE,
  }),
});
