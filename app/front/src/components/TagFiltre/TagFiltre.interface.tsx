export type TagFiltreProps = {
  libellé: string;
  auClic: (estAppuyé: boolean) => void;
  taille?: "petit";
  emoji?: string;
  appuyéParDéfaut?: boolean;
};
