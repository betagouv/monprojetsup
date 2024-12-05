import { z } from "zod";

export const formationsValidationSchema = z.object({
  formationsFavorites: z
    .object({
      id: z.string(),
      niveauAmbition: z.number().nullable(),
      commentaire: z.string().nullable(),
    })
    .array(),
});
