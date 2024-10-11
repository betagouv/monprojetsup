import { type InscriptionÉlèveStore } from "./useInscriptionÉlève.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { create } from "zustand";

const useInscriptionÉlèveStore = create<InscriptionÉlèveStore>((set, get) => ({
  étapes: [
    {
      titreÉtape: i18n.ÉLÈVE.PROJET.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.PROJET.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/projet",
    },
    {
      titreÉtape: i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.SCOLARITÉ.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/scolarite",
    },
    {
      titreÉtape: i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.DOMAINES.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/domaines",
    },
    {
      titreÉtape: i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.INTÉRÊTS.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/interets",
    },
    {
      titreÉtape: i18n.ÉLÈVE.MÉTIERS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.MÉTIERS.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/metiers",
    },
    {
      titreÉtape: i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.ÉTUDE.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/etude",
    },
    {
      titreÉtape: i18n.ÉLÈVE.FORMATIONS.PARCOURS_INSCRIPTION.TITRE_ÉTAPE,
      titre: i18n.ÉLÈVE.FORMATIONS.PARCOURS_INSCRIPTION.TITRE,
      url: "/eleve/inscription/formations",
    },
    {
      titreÉtape: i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.TITRE_PAGE,
      titre: i18n.ÉLÈVE.CONFIRMATION_INSCRIPTION.TITRE_PAGE,
      url: "/eleve/inscription/confirmation",
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

export const actionsInscriptionÉlèveStore = () => useInscriptionÉlèveStore((étatActuel) => étatActuel.actions);
export const étapesInscriptionÉlèveStore = () => useInscriptionÉlèveStore((étatActuel) => étatActuel.étapes);
export const indexÉtapeActuelleInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.indexÉtapeActuelle);
export const étapeActuelleInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.étapeActuelle);
export const étapeSuivanteInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.étapeSuivante);
export const étapePrécédenteInscriptionÉlèveStore = () =>
  useInscriptionÉlèveStore((étatActuel) => étatActuel.étapePrécédente);
