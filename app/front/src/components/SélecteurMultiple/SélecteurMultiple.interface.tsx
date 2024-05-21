export type SélecteurMultipleProps = {
  label: string;
  texteOptionsSélectionnées: string;
  optionsSuggérées: SélecteurMultipleOption[];
  auChangementOptionsSélectionnées: (optionsSélectionnées: SélecteurMultipleOption[]) => void;
  àLaRechercheDUneOption: (recherche: string) => void;
  rechercheSuggestionsEnCours: boolean;
  description?: string;
  optionsSélectionnéesParDéfaut?: SélecteurMultipleOption[];
};

export type SélecteurMultipleOption = {
  valeur: string;
  label: string;
};
