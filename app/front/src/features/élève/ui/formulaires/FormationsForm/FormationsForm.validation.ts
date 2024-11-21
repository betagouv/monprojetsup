import { z } from "zod";

export const formationsValidationSchema = z.object({
  formationsFavorites: z
    .object({
      id: z.string(),
      niveauAmbition: z.number().nullable(),
      voeux: z.string().array(),
      commentaire: z.string().nullable(),
    })
    .array(),
});
