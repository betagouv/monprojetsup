import { z } from "zod";

export const intêretsValidationSchema = z.object({
  intêrets: z.string().array().optional(),
});
