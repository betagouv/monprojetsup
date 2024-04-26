export type Élève = {
  id: string;
  situation?: SituationÉlève;
  classe?: ClasseÉlève;
  bac?: string;
  spécialités?: string[];
  domaines?: string[];
};

export const situationÉlève = ["aucune_idee", "quelques_pistes", "projet_precis"] as const;
export type SituationÉlève = (typeof situationÉlève)[number];

export const classeÉlève = ["seconde", "seconde_sthr", "seconde_tmd", "première", "terminale"] as const;
export type ClasseÉlève = (typeof classeÉlève)[number];
