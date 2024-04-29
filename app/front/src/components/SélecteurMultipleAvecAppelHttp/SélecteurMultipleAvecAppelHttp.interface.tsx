export type SélecteurMultipleAvecAppelHttpProps = {
  label: string;
  texteOptionsSélectionnées: string;
  optionsSuggérées: SélecteurMultipleAvecAppelHttpOption[];
  auChangementOptionsSélectionnées: (optionsSélectionnées: SélecteurMultipleAvecAppelHttpOption[]) => void;
  àLaRechercheDUneOption: (recherche: string) => void;
  rechercheMétiersEnCours: boolean;
  description?: string;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  optionsSélectionnéesParDéfaut?: SélecteurMultipleAvecAppelHttpOption[];
};

export type SélecteurMultipleAvecAppelHttpOption = {
  valeur: string;
  label: string;
};
