import { z } from "zod";

export const formationsValidationSchema = z.object({
  formations: z.string().array().optional(),
});
