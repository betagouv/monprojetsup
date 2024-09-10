import { type ReactNode } from "react";

export type useChampDeSaisieSqueletteArgs = {
  id: ChampDeSaisieSqueletteProps["id"];
  status: ChampDeSaisieSqueletteProps["status"];
  placeholder: ChampDeSaisieSqueletteProps["placeholder"];
  obligatoire: ChampDeSaisieSqueletteProps["obligatoire"];
  auChangement: ChampDeSaisieSqueletteProps["auChangement"];
};

export type ChampDeSaisieSqueletteProps = {
  id: string;
  entête: AvecTitre | SansTitre;
  status?: {
    type: "désactivé" | "erreur" | "succès";
    message?: string;
  };
  placeholder?: string;
  obligatoire?: boolean;
  auChangement?: React.ChangeEventHandler<HTMLInputElement>;
  children: ReactNode;
};

type AvecTitre = {
  label: string;
  description?: string;
};

type SansTitre = {
  labelAccessibilité: string;
};
