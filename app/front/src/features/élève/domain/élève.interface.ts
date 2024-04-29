export type Élève = {
  id: string;
  situation?: SituationÉlève;
  classe?: ClasseÉlève;
  bac?: string;
  spécialités?: string[];
  domaines?: string[];
  centresIntêrets?: string[];
  situationMétiers?: SituationMétiersÉlève;
  métiers?: string[];
};

export const situationÉlève = ["aucune_idee", "quelques_pistes", "projet_precis"] as const;
export type SituationÉlève = (typeof situationÉlève)[number];

export const situationMétiersÉlève = ["aucune_idee", "quelques_pistes"] as const;
export type SituationMétiersÉlève = (typeof situationMétiersÉlève)[number];

export const classeÉlève = ["seconde", "seconde_sthr", "seconde_tmd", "première", "terminale"] as const;
export type ClasseÉlève = (typeof classeÉlève)[number];
