export type TagCliquableProps = {
  libellé: string;
  auClic: () => void;
  taille?: "petit";
  supprimable?: boolean;
};
