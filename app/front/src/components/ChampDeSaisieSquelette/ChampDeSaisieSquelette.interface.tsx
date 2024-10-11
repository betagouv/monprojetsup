import { type StatusFormulaire } from "@/types/commons";
import { type ReactNode } from "react";

export type useChampDeSaisieSqueletteArgs = {
  id: ChampDeSaisieSqueletteProps["id"];
  status?: ChampDeSaisieSqueletteProps["status"];
  placeholder?: ChampDeSaisieSqueletteProps["placeholder"];
  obligatoire?: ChampDeSaisieSqueletteProps["obligatoire"];
  auChangement?: ChampDeSaisieSqueletteProps["auChangement"];
};

export type ChampDeSaisieSqueletteProps = {
  id: string;
  entête: AvecTitre | SansTitre;
  status?: StatusFormulaire;
  placeholder?: string;
  obligatoire?: boolean;
  auChangement?: React.ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement>;
  children: ReactNode;
};

type AvecTitre = {
  label: string;
  description?: string;
};

type SansTitre = {
  labelAccessibilité: string;
};
