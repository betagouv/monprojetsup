import { type ÉtapesInscriptionÉlèveStore } from "./useÉtapesInscriptionÉlève.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { create } from "zustand";

const useÉtapesInscriptionÉlèveStore = create<ÉtapesInscriptionÉlèveStore>((set, get) => ({
  étapes: [
    {
      titreÉtape: i18n.ÉLÈVE.PROJET.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.PROJET.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/projet",
    },
    {
      titreÉtape: i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/scolarite",
    },
    {
      titreÉtape: i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/domaines",
    },
    {
      titreÉtape: i18n.ÉLÈVE.INTÊRETS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.INTÊRETS.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/interets",
    },
    {
      titreÉtape: i18n.ÉLÈVE.MÉTIERS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.MÉTIERS.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/metiers",
    },
    {
      titreÉtape: i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/etude",
    },
    {
      titreÉtape: i18n.ÉLÈVE.FORMATIONS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.FORMATIONS.PARCOURS_INSCRIPTION.TITRE,
      url: "/inscription/formations",
    },
    {
      titreÉtape: i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.TITRE_PAGE,
      titre: i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.TITRE_PAGE,
      url: "/inscription/confirmation",
    },
  ],
  indexÉtapeActuelle: undefined,
  étapeActuelle: undefined,
  étapeSuivante: undefined,
  étapePrécédente: undefined,
  actions: {
    définirÉtapeActuelle: (url: string) => {
      const étapeActuelle = get().étapes.find((étape) => url.endsWith(étape.url));
      const indexÉtapeActuelle = get().étapes.findIndex((étape) => étape.url === étapeActuelle?.url);

      set({
        indexÉtapeActuelle,
        étapeActuelle,
        étapeSuivante: get().étapes[indexÉtapeActuelle + 1],
        étapePrécédente: get().étapes[indexÉtapeActuelle - 1],
      });
    },
  },
}));

export const actionsÉtapesInscriptionÉlèveStore = () =>
  useÉtapesInscriptionÉlèveStore((étatActuel) => étatActuel.actions);
export const étapesÉtapesInscriptionÉlèveStore = () =>
  useÉtapesInscriptionÉlèveStore((étatActuel) => étatActuel.étapes);
export const indexÉtapeActuelleÉtapesInscriptionÉlèveStore = () =>
  useÉtapesInscriptionÉlèveStore((étatActuel) => étatActuel.indexÉtapeActuelle);
export const étapeActuelleÉtapesInscriptionÉlèveStore = () =>
  useÉtapesInscriptionÉlèveStore((étatActuel) => étatActuel.étapeActuelle);
export const étapeSuivanteÉtapesInscriptionÉlèveStore = () =>
  useÉtapesInscriptionÉlèveStore((étatActuel) => étatActuel.étapeSuivante);
export const étapePrécédenteÉtapesInscriptionÉlèveStore = () =>
  useÉtapesInscriptionÉlèveStore((étatActuel) => étatActuel.étapePrécédente);
