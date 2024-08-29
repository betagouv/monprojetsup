import { z } from "zod";

export const formationsValidationSchema = z.object({
  formationsFavorites: z
    .object({
      id: z.string(),
      niveauAmbition: z.null(),
      tripletsAffectationsChoisis: z.string().array(),
      commentaire: z.null(),
    })
    .array(),
});
