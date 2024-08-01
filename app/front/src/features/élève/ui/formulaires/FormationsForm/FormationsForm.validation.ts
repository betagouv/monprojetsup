import { z } from "zod";

export const formationsValidationSchema = z.object({
  formationsFavorites: z.string().array(),
});
