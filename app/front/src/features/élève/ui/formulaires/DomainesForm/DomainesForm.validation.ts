import { i18n } from "@/configuration/i18n/i18n";
import { z } from "zod";

export const domainesValidationSchema = z.object({
  domaines: z.string().array().nonempty(i18n.ÉLÈVE.DOMAINES.SÉLECTIONNE_AU_MOINS_UN),
});
