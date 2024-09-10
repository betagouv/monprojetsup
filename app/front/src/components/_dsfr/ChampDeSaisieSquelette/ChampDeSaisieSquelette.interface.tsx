import { type ReactNode } from "react";

export type useChampDeSaisieSqueletteArgs = {
  id: ChampDeSaisieSqueletteProps["id"];
  status: ChampDeSaisieSqueletteProps["status"];
  obligatoire: ChampDeSaisieSqueletteProps["obligatoire"];
  auChangement: ChampDeSaisieSqueletteProps["auChangement"];
};

export type ChampDeSaisieSqueletteProps = {
  id: string;
  label: string;
  description?: string;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  obligatoire?: boolean;
  auChangement?: React.ChangeEventHandler<HTMLInputElement>;
  children: ReactNode;
};
