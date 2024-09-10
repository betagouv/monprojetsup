import { type ChampDeSaisieSqueletteProps } from "@/components/_dsfr/ChampDeSaisieSquelette/ChampDeSaisieSquelette.interface";

export type ChampDeRechercheProps = {
  label: ChampDeSaisieSqueletteProps["label"];
  description: ChampDeSaisieSqueletteProps["description"];
  status: ChampDeSaisieSqueletteProps["status"];
  auChangement: ChampDeSaisieSqueletteProps["auChangement"];
  obligatoire: ChampDeSaisieSqueletteProps["obligatoire"];
  registerHookForm?: {};
};
