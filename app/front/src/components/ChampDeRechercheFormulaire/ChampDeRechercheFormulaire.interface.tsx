import { type ChampDeSaisieSqueletteProps } from "@/components/_dsfr/ChampDeSaisieSquelette/ChampDeSaisieSquelette.interface";

export type ChampDeRechercheFormulaireProps = {
  entête: ChampDeSaisieSqueletteProps["entête"];
  àLaSoumission: (recherche: string) => Promise<void>;
  auChangement?: ChampDeSaisieSqueletteProps["auChangement"];
  status?: ChampDeSaisieSqueletteProps["status"];
  placeholder?: ChampDeSaisieSqueletteProps["placeholder"];
  valeurParDéfaut?: string;
  obligatoire?: ChampDeSaisieSqueletteProps["obligatoire"];
  registerHookForm?: {};
};
