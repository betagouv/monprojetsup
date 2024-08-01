import { z } from "zod";

export const métiersValidationSchema = z.object({
  métiersFavoris: z.string().array(),
});
