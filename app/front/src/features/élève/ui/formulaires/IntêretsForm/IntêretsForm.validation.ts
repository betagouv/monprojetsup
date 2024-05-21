import { i18n } from "@/configuration/i18n/i18n";
import { z } from "zod";

export const centresIntêretsValidationSchema = z.object({
  centresIntêrets: z.string().array().nonempty(i18n.ÉLÈVE.INTÊRETS.SÉLECTIONNE_AU_MOINS_UN),
});
