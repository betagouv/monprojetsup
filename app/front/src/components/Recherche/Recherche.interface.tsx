export type RechercheProps<T> = {
  rechercheCallback: (recherche: string) => T[];
  label: string;
  description?: string;
  nombreDeCaractèresMinimumRecherche: number;
};

export type UseRechercheArgs<T> = {
  nombreDeCaractèresMinimumRecherche: RechercheProps<T>["nombreDeCaractèresMinimumRecherche"];
  rechercheCallback: RechercheProps<T>["rechercheCallback"];
};
