import { type AlternanceÉlève, type DuréeÉtudesPrévueÉlève } from "./élève.interface";
import { i18n } from "@/configuration/i18n/i18n";

export const duréeÉtudesPrévueVersTraduction = (duréeÉtudesPrévue: DuréeÉtudesPrévueÉlève) => {
  switch (duréeÉtudesPrévue) {
    case "aucune_idee":
      return i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.AUCUNE_IDÉE.LABEL;
    case "courte":
      return i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.COURTE.LABEL;
    case "longue":
      return i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.LONGUE.LABEL;
    default:
      return i18n.ÉLÈVE.ÉTUDE.DURÉE_ÉTUDES.OPTIONS.INDIFFÉRENT.LABEL;
  }
};

export const alternanceVersTraduction = (alternance: AlternanceÉlève) => {
  switch (alternance) {
    case "pas_interesse":
      return i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.PAS_INTÉRESSÉ.LABEL;
    case "interesse":
      return i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.INTÉRESSÉ.LABEL;
    case "tres_interesse":
      return i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.TRÈS_INTÉRESSÉ.LABEL;
    default:
      return i18n.ÉLÈVE.ÉTUDE.ALTERNANCE.OPTIONS.INDIFFÉRENT.LABEL;
  }
};
