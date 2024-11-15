import { type ChampDeSaisieSqueletteProps } from "@/components/ChampDeSaisieSquelette/ChampDeSaisieSquelette.interface";
import { UseFormRegisterReturn } from "react-hook-form";

export type ChampDeRechercheFormulaireProps = {
  entête: ChampDeSaisieSqueletteProps["entête"];
  àLaSoumission: (recherche: string) => void;
  auChangement?: ChampDeSaisieSqueletteProps["auChangement"];
  status?: ChampDeSaisieSqueletteProps["status"];
  placeholder?: ChampDeSaisieSqueletteProps["placeholder"];
  valeurParDéfaut?: string;
  obligatoire?: ChampDeSaisieSqueletteProps["obligatoire"];
  registerHookForm?: UseFormRegisterReturn;
};
