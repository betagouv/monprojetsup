export type ContrôleSegmentéProps = {
  légende: string;
  éléments: Array<{ valeur: string; label: string }>;
  auClic: (catégorieSélectionnée: string) => void;
  valeurSélectionnéeParDéfaut: string;
};
