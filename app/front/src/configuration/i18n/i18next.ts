import commun from "./locales/fr/commun.json";
import ficheFormation from "./locales/fr/ficheFormation.json";
import { use } from "i18next";
import { initReactI18next } from "react-i18next";

export const defaultNS = "commun";
export const resources = {
  fr: {
    commun,
    ficheFormation,
  },
} as const;

const i18next = use(initReactI18next).init({
  resources,
  ns: ["commun", "ficheFormation"],
  defaultNS,
  lng: "fr",
  interpolation: {
    escapeValue: false,
  },
});

export default i18next;
