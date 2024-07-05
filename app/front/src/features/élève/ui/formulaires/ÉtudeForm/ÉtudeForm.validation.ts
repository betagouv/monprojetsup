import { i18n } from "@/configuration/i18n/i18n";
import { alternanceÉlève, duréeÉtudesPrévueÉlève } from "@/features/élève/domain/élève.interface";
import { z } from "zod";

export const étudeValidationSchema = z.object({
  duréeÉtudesPrévue: z.enum([...duréeÉtudesPrévueÉlève, ""], {
    errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
  }),
  alternance: z.enum([...alternanceÉlève, ""], {
    errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
  }),
  villes: z
    .object({
      codeInsee: z.string(),
      nom: z.string(),
      latitude: z.number(),
      longitude: z.number(),
    })
    .array()
    .optional(),
});
