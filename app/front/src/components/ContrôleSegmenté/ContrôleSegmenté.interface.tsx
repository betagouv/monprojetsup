type CatégorieContrôleSegmenté = {
  valeur: "première" | "seconde";
  label: string;
};

export type ContrôleSegmentéProps = {
  légende: string;
  éléments: CatégorieContrôleSegmenté[];
  auClic: (catégorieSélectionnée: CatégorieContrôleSegmenté["valeur"]) => void;
  valeurSélectionnéeParDéfaut: CatégorieContrôleSegmenté["valeur"];
};
