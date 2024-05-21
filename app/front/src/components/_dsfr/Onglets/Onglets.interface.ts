import { type ReactNode } from "react";

export interface OngletsProps {
  onglets: Array<{ titre: string; contenu: ReactNode }>;
  ongletAffichéParDéfaut?: number;
  nomAccessible: string;
}
