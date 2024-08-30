export type TagFiltreProps = {
  libellé: string;
  emoji: string;
  auClic: (estAppuyé: boolean) => void;
  taille?: "petit";
  appuyéParDéfaut?: boolean;
};
