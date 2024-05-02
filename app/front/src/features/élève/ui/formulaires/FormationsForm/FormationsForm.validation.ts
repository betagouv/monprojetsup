import { i18n } from "@/configuration/i18n/i18n";
import { situationFormationsÉlève } from "@/features/élève/domain/élève.interface";
import { z } from "zod";

export const formationsValidationSchema = z.object({
  situationFormations: z.enum(situationFormationsÉlève, {
    errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
  }),
  formations: z.string().array().optional(),
});
