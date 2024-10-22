import { type ReactNode } from "react";

export type TagFiltreAvecEmojiProps = {
  children: ReactNode;
  auClic: (estAppuyé: boolean) => void;
  emoji: string;
  appuyéParDéfaut?: boolean;
};
