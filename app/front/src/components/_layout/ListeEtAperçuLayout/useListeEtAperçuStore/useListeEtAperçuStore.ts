import { type ListeEtAperçuStore, type ListeEtAperçuStoreState } from "./useListeEtAperçuStore.interface";
import { create } from "zustand";

const étatInitial: ListeEtAperçuStoreState = {
  élémentAffiché: {
    id: null,
    type: "formation",
  },
  afficherBarreLatéraleEnMobile: true,
  recherche: undefined,
};

export const useListeEtAperçuStore = create<ListeEtAperçuStore>((set) => ({
  ...étatInitial,
  actions: {
    rechercher: (recherche: string) => {
      set({
        recherche,
      });
    },
    réinitialiserRecherche: () => {
      set({ recherche: undefined });
    },
    changerÉlémentAffiché: (élémentAffiché: NonNullable<ListeEtAperçuStoreState["élémentAffiché"]>) => {
      set({
        élémentAffiché,
      });
      window.location.hash = élémentAffiché.id ?? "";
    },
    changerAfficherBarreLatéraleEnMobile: (afficher: boolean) => {
      set({
        afficherBarreLatéraleEnMobile: afficher,
      });
    },
    réinitialiserStore: () => {
      set(étatInitial);
    },
  },
}));

export const actionsListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.actions);
export const élémentAffichéListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.élémentAffiché);
export const afficherBarreLatéraleEnMobileListeEtAperçuStore = () =>
  useListeEtAperçuStore((étatActuel) => étatActuel.afficherBarreLatéraleEnMobile);
export const rechercheListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.recherche);
