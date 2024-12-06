/* eslint-disable sonarjs/redundant-type-aliases */
export type Favori = {
  id: string;
  nom: string;
  estFavori: boolean;
  ariaLabel?: string;
  title?: string;
  url?: string;
  désactivé?: boolean;
  icôneEstFavori?: string;
  icôneEstPasFavori?: string;
  callbackMettreÀJour?: (idFavori: string) => Promise<unknown>;
};

export type FavoriProps = Favori;
