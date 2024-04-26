import { z } from "zod";

export const centresIntêretsValidationSchema = z.object({
  centresIntêrets: z.string().array().optional(),
});
