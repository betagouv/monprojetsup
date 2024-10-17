import { type ToastStore, ToastStoreState } from "./useToast.interface";
import { AlertProps } from "@codegouvfr/react-dsfr/Alert";
import { create } from "zustand";

const étatInitial: ToastStoreState = {
  estOuvert: false,
  titre: undefined,
  description: undefined,
  type: undefined,
};

const useToastStore = create<ToastStore>((set) => ({
  ...étatInitial,
  actions: {
    déclencherToast: (titre: string, description: string, type: AlertProps["severity"]) => {
      set({
        estOuvert: true,
        titre,
        description,
        type,
      });
    },
    fermerToast: () => {
      set(étatInitial);
    },
  },
}));

export const actionsToastStore = () => useToastStore((étatActuel) => étatActuel.actions);
export const estOuvertToastStore = () => useToastStore((étatActuel) => étatActuel.estOuvert);
export const titreToastStore = () => useToastStore((étatActuel) => étatActuel.titre);
export const descriptionToastStore = () => useToastStore((étatActuel) => étatActuel.description);
export const typeToastStore = () => useToastStore((étatActuel) => étatActuel.type);
