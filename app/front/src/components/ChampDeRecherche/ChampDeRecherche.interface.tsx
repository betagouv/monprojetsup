import { type ChampDeSaisieSqueletteProps } from "@/components/ChampDeSaisieSquelette/ChampDeSaisieSquelette.interface";

export type ChampDeRechercheProps = {
  entête: ChampDeSaisieSqueletteProps["entête"];
  status?: ChampDeSaisieSqueletteProps["status"];
  placeholder?: ChampDeSaisieSqueletteProps["placeholder"];
  auChangement?: ChampDeSaisieSqueletteProps["auChangement"];
  obligatoire?: ChampDeSaisieSqueletteProps["obligatoire"];
  registerHookForm?: {};
};
