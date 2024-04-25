import { z } from "zod";

export const domainesValidationSchema = z.object({
  domaines: z.string().array().optional(),
});
