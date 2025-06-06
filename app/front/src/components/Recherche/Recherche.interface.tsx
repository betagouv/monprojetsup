export type RechercheProps = {
  rechercheCallback: (recherche?: string) => Promise<void> | void;
  label: string;
  nombreDeCaractèresMinimumRecherche: number;
  nombreDeCaractèresMaximumRecherche: number;
  description?: string;
  nombreDeRésultats?: number;
};

export type UseRechercheArgs = {
  nombreDeCaractèresMinimumRecherche: RechercheProps["nombreDeCaractèresMinimumRecherche"];
  nombreDeCaractèresMaximumRecherche: RechercheProps["nombreDeCaractèresMaximumRecherche"];
  rechercheCallback: RechercheProps["rechercheCallback"];
  nombreDeRésultats: RechercheProps["nombreDeRésultats"];
};
