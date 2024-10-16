import { ReactNode } from "react";

export type SélecteurMultipleOption = {
  valeur: string;
  label: string;
};

export type SélecteurMultipleProps = {
  label: string;
  texteOptionsSélectionnées: string;
  optionsSuggérées: SélecteurMultipleOption[];
  auChangementOptionsSélectionnées: (optionsSélectionnées: SélecteurMultipleOption[]) => void;
  àLaRechercheDUneOption: (recherche: string) => void;
  rechercheSuggestionsEnCours: boolean;
  nombreDeCaractèreMinimumRecherche: number;
  description?: string;
  optionsSélectionnéesParDéfaut?: SélecteurMultipleOption[];
  nombreDeSuggestionsMax?: number;
  messageNbSuggestionsMaxDépassé?: ReactNode;
  forcerRafraichissementOptionsSélectionnées?: boolean;
};

export type UseSélecteurMultipleArgs = {
  nombreDeCaractèreMinimumRecherche: SélecteurMultipleProps["nombreDeCaractèreMinimumRecherche"];
  àLaRechercheDUneOption: SélecteurMultipleProps["àLaRechercheDUneOption"];
  auChangementOptionsSélectionnées: SélecteurMultipleProps["auChangementOptionsSélectionnées"];
  rechercheSuggestionsEnCours: SélecteurMultipleProps["rechercheSuggestionsEnCours"];
  optionsSélectionnéesParDéfaut: SélecteurMultipleProps["optionsSélectionnéesParDéfaut"];
  optionsSuggérées: SélecteurMultipleProps["optionsSuggérées"];
  nombreDeSuggestionsMax: SélecteurMultipleProps["nombreDeSuggestionsMax"];
  forcerRafraichissementOptionsSélectionnées: SélecteurMultipleProps["forcerRafraichissementOptionsSélectionnées"];
};
