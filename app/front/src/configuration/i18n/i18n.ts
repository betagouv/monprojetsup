import { localeFR } from "./locales/localeFR";

const locales: Record<"fr", typeof localeFR> = {
  fr: localeFR,
};

export const i18n = locales["fr"];
