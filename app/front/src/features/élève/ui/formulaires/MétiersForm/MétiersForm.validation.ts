import { z } from "zod";

export const métiersValidationSchema = z.object({
  métiers: z.string().array().optional(),
});
