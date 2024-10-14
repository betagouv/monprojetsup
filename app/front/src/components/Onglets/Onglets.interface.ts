import { type ReactNode } from "react";

export type OngletsProps = {
  onglets: Array<{ titre: string; contenu: ReactNode }>;
  ongletAffichéParDéfaut?: number;
  nomAccessible: string;
};
