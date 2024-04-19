import { i18n } from "@/configuration/i18n/i18n";
import { classeÉlève } from "@/features/élève/domain/élève.interface";
import { z } from "zod";

export const scolaritéValidationSchema = z.object({
  classe: z.enum(classeÉlève, {
    errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
  }),
});
