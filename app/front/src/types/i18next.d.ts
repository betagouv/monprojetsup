import type Resources from "./i18next-generated";

declare module "i18next" {
  interface CustomTypeOptions {
    defaultNS: "commun";
    resources: Resources;
  }
}
