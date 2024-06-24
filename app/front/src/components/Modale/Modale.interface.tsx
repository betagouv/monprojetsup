import { type ReactNode } from "react";

export type ModaleProps = {
  id: string;
  titre: string;
  boutons?: ReactNode;
  children: ReactNode;
};
