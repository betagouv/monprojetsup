import { i18n } from "@/configuration/i18n/i18n";
import { situationÉlève } from "@/features/élève/domain/élève.interface";
import { z } from "zod";

export const projetValidationSchema = z.object({
  situation: z.enum(situationÉlève, {
    errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
  }),
});
