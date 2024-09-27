import { i18n } from "@/configuration/i18n/i18n";
import { classeÉlève } from "@/features/élève/domain/élève.interface";
import { type Bac } from "@/features/référentielDonnées/domain/référentielDonnées.interface";
import { z } from "zod";

export const scolaritéValidationSchema = (bacs: Bac[]) => {
  const bacIdsAutorisés = bacs.map((bac) => bac.id);

  return z.object({
    classe: z.enum(classeÉlève, {
      errorMap: () => ({ message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE }),
    }),
    bac: z
      .string()
      .nullable()
      .refine((valeur) => !valeur || bacIdsAutorisés.includes(valeur), {
        message: i18n.COMMUN.ERREURS_FORMULAIRES.LISTE_OBLIGATOIRE,
      }),
    moyenneGénérale: z.number().gte(-1).lte(20).nullable(),
    spécialités: z.string().array(),
  });
};
