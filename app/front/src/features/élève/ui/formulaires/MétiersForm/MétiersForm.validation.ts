import { i18n } from "@/configuration/i18n/i18n";
import { situationMétiersÉlève } from "@/features/élève/domain/élève.interface";
import { z } from "zod";

export const métiersValidationSchema = z.object({
  situationMétiers: z.enum(situationMétiersÉlève, {
    errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
  }),
});
