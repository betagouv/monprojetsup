export type TagFiltreProps = {
  libellé: string;
  auClic: (estAppuyé: boolean) => void;
  taille?: "petit";
  appuyéParDéfaut?: boolean;
};
