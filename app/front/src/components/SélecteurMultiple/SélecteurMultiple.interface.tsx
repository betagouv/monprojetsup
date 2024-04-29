export type SélecteurMultipleProps = {
  label: string;
  texteOptionsSélectionnées: string;
  optionsSuggérées: SélecteurMultipleOption[];
  auChangementOptionsSélectionnées: (optionsSélectionnées: SélecteurMultipleOption[]) => void;
  àLaRechercheDUneOption: (recherche: string) => void;
  rechercheMétiersEnCours: boolean;
  description?: string;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  optionsSélectionnéesParDéfaut?: SélecteurMultipleOption[];
};

export type SélecteurMultipleOption = {
  valeur: string;
  label: string;
};
