export type Élève = {
  id: string;
  situation?: SituationÉlève;
};

export const situationÉlève = ["aucune_idee", "quelques_pistes", "projet_precis"] as const;
export type SituationÉlève = (typeof situationÉlève)[number];
