import { i18n } from "@/configuration/i18n/i18n";
import { z } from "zod";

export const centresIntérêtsValidationSchema = z.object({
  centresIntérêts: z.string().array().nonempty(i18n.ÉLÈVE.INTÉRÊTS.SÉLECTIONNE_AU_MOINS_UN),
});
